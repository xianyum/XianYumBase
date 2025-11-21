package cn.xianyum.starter;

import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.proxy.infra.container.ProxyServerContainer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

// 排除原生数据源自动配置，避免与动态数据源冲突
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("cn.xianyum.**.dao")
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = {"cn.xianyum"})
public class XianyumApplication {

    public static Logger log = LoggerFactory.getLogger(XianyumApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(XianyumApplication.class, args);
        ProxyServerContainer proxyServerContainer = new ProxyServerContainer();
        proxyServerContainer.start();
        Environment env = run.getEnvironment();
        printStartupLog(env);
    }

    public static void printStartupLog(Environment env) {
        String STARTUP_MSG = "\n----------------------------------------------------------" +
                "\n应用 '{}' 启动成功，JDK版本号：{} ！" +
                "\nswagger文档：http://{}:{}{}/swagger-ui/index.html" +
                "\ndruid地址：http://{}:{}/druid/login.html" +
                "\n当前环境变量：{}" +
                "\n----------------------------------------------------------";
        // 获取配置参数
        String appName = env.getProperty("spring.application.name", "unknown");
        String jdkVersion = env.getProperty("java.version", "");
        String hostIp = IPUtils.getHostIp();
        String serverPort = env.getProperty("server.port", "");
        String contextPath = env.getProperty("server.servlet.context-path", ""); // 应用上下文路径（默认空）
        String activeEnv = env.getProperty("spring.profiles.active", "default");
        log.info(STARTUP_MSG,
                appName,          // 占位符1：应用名
                jdkVersion,       // 占位符2：JDK版本
                hostIp,           // 占位符3：swagger - IP
                serverPort,       // 占位符4：swagger - 端口
                contextPath,      // 占位符5：swagger - 上下文路径
                hostIp,           // 占位符6：druid - IP
                serverPort,       // 占位符7：druid - 端口
                activeEnv         // 占位符8：当前环境
        );
    }
}
