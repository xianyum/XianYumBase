package cn.xianyum.framwork.config;

import cn.xianyum.common.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author xianyum
 * @date 2025/11/17 22:27
 */
@Component
@Slf4j
public class StartUpInfoPrinter implements CommandLineRunner {

    private final Environment environment;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.profiles.active:default}")
    private String activeProfiles;

    public StartUpInfoPrinter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        String host = IPUtils.getHostIp();
        String swaggerPath = "/swagger-ui/index.html";
        String localUrl = "http://" + host + ":" + serverPort + contextPath;
        String swaggerUrl = localUrl + swaggerPath;

        String startupInfo = "\n======= 服务启动信息 =======\n"
                + "激活环境: " + activeProfiles + "\n"
                + "本地访问地址: " + localUrl + "\n"
                + "Swagger文档地址: " + swaggerUrl;

        log.info(startupInfo);
    }
}
