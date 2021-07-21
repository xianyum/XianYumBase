package cn.xianyum.proxy.container;

import cn.xianyum.common.utils.PropertiesUtil;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.proxy.common.utils.IdleCheckHandler;
import cn.xianyum.proxy.common.utils.ProxyMessageDecoder;
import cn.xianyum.proxy.common.utils.ProxyMessageEncoder;
import cn.xianyum.proxy.handlers.ProxyChangedListener;
import cn.xianyum.proxy.handlers.ServerChannelHandler;
import cn.xianyum.proxy.handlers.UserChannelHandler;
import cn.xianyum.proxy.metrics.handler.BytesMetricsHandler;
import cn.xianyum.proxy.service.ProxyDetailsService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.BindException;
import java.util.List;


public class ProxyServerContainer implements Container, ProxyChangedListener {

    private boolean needsClientAuth = PropertiesUtil.getBoolean("proxy.ssl.needsClientAuth",false);

    private String jksPath = PropertiesUtil.getString("proxy.ssl.jksPath");

    private int sslPort = PropertiesUtil.getInt("proxy.ssl.port");

    private boolean sslEnable = PropertiesUtil.getBoolean("proxy.ssl.enable",false);

    private String sslBind = PropertiesUtil.getString("proxy.ssl.bind");

    private int serverPort = PropertiesUtil.getInt("server.port");

    /**
     * max packet is 2M.
     */
    private static final int MAX_FRAME_LENGTH = 2 * 1024 * 1024;

    private static final int LENGTH_FIELD_OFFSET = 0;

    private static final int LENGTH_FIELD_LENGTH = 4;

    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private static final int LENGTH_ADJUSTMENT = 0;

    private static Logger logger = LoggerFactory.getLogger(ProxyServerContainer.class);

    private NioEventLoopGroup serverWorkerGroup;

    private NioEventLoopGroup serverBossGroup;

    public ProxyServerContainer() {
        serverBossGroup = new NioEventLoopGroup(4);
        serverWorkerGroup = new NioEventLoopGroup(8);
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProxyMessageDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
                ch.pipeline().addLast(new ProxyMessageEncoder());
                ch.pipeline().addLast(new IdleCheckHandler(IdleCheckHandler.READ_IDLE_TIME, IdleCheckHandler.WRITE_IDLE_TIME, 0));
                ch.pipeline().addLast(new ServerChannelHandler());
            }
        });

        try {
            bootstrap.bind("0.0.0.0", serverPort);
            logger.info("proxy server start on port " + serverPort);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (sslEnable) {
            String host = sslBind;
            int port = sslPort;
            initializeSSLTCPTransport(host, port, new SslContextCreator().initSSLContext());
        }

        startUserPort();

    }

    private void initializeSSLTCPTransport(String host, int port, final SSLContext sslContext) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                try {
                    pipeline.addLast("ssl", createSslHandler(sslContext,  needsClientAuth));
                    ch.pipeline().addLast(new ProxyMessageDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
                    ch.pipeline().addLast(new ProxyMessageEncoder());
                    ch.pipeline().addLast(new IdleCheckHandler(IdleCheckHandler.READ_IDLE_TIME, IdleCheckHandler.WRITE_IDLE_TIME, 0));
                    ch.pipeline().addLast(new ServerChannelHandler());
                } catch (Throwable th) {
                    logger.error("Severe error during pipeline creation", th);
                    throw th;
                }
            }
        });
        try {

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(host, port);
            f.sync();
            logger.info("proxy ssl server start on port {}", port);
        } catch (InterruptedException ex) {
            logger.error("An interruptedException was caught while initializing server", ex);
        }
    }

    private void startUserPort() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addFirst(new BytesMetricsHandler());
                ch.pipeline().addLast(new UserChannelHandler());
            }
        });

        List<Integer> ports = SpringUtils.getBean(ProxyDetailsService.class).getUserPorts();
        for (int port : ports) {
            try {
                bootstrap.bind(port).get();
            } catch (Exception ex) {

                // BindException表示该端口已经绑定过
                if (!(ex.getCause() instanceof BindException)) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    @Override
    public void onChanged() {
        startUserPort();
    }

    @Override
    public void stop() {
        serverBossGroup.shutdownGracefully();
        serverWorkerGroup.shutdownGracefully();
    }

    private ChannelHandler createSslHandler(SSLContext sslContext, boolean needsClientAuth) {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        if (needsClientAuth) {
            sslEngine.setNeedClientAuth(true);
        }

        return new SslHandler(sslEngine);
    }

}
