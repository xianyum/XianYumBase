package cn.xianyum.mqtt.config;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.mqtt.infra.handler.AbstractMqttInterceptHandler;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.moquette.broker.Server;
import java.util.Collections;
import java.util.Properties;

/**
 * @author xianyum
 * @date 2026/1/4 19:58
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttBrokerConfig {

    // 注入Topic路由管理器
    private final MqttTopicRouterConfig mqttTopicRouter;

    private Server mqttServer;

    /** 认证配置 */
    private final MqttConfigProperties mqttConfigProperties;

    private final MqttAuthenticator mqttAuthenticator;

    @PostConstruct
    public void startMqttBroker() {
        try {
            Properties props = new Properties();
            props.setProperty(Constants.MQTT_PORT_FIELD, mqttConfigProperties.getPort());
            props.setProperty(Constants.MQTT_HOST_FIELD, mqttConfigProperties.getHost());
            props.setProperty("persistence", "false");
            // 禁用UUID文件
            props.setProperty("uuid_file", "false");
            IConfig config = new MemoryConfig(props);

            // 改造拦截器：调用路由管理器处理消息
            InterceptHandler publishHandler = new AbstractMqttInterceptHandler() {
                @Override
                public void onPublish(InterceptPublishMessage msg) {
                    try {
                        // 核心修改：交给路由管理器匹配处理器
                        mqttTopicRouter.route(msg);
                    } catch (Exception e) {
                        log.error("路由MQTT消息失败", e);
                    }
                }
            };
            // 3. 初始化并启动MQTT服务端
            mqttServer = new Server();

            // 仅传入配置和拦截器列表，内部自动初始化存储/网络组件
            mqttServer.startServer(config, Collections.singletonList(publishHandler),null,mqttAuthenticator,null);
            log.info("嵌入式MQTT服务端启动成功 -> {}:{}", mqttConfigProperties.getHost(), mqttConfigProperties.getPort());
        } catch (Exception e) {
            log.error("启动MQTT服务端失败", e);
            throw new RuntimeException("MQTT服务端启动失败", e);
        }
    }

    @PreDestroy
    public void stopMqttBroker() {
        if (mqttServer != null) {
            mqttServer.stopServer();
            log.info("嵌入式MQTT服务端已关闭");
        }
    }

    @Bean
    public Server mqttServer() {
        return mqttServer;
    }
}
