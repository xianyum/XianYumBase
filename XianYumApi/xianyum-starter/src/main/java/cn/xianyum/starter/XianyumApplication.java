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

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class}) //去掉springboot 默认的数据源配置
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
        String msg = "\n----------------------------------------------------------\n应用 '{}' 启动成功， JDK版本号：{} ！\nswagger文档：http://{}:{}{}/swagger-ui/index.html\nnacos地址：http://{}/nacos\n当前环境变量：{}";
        log.info(msg,env.getProperty("spring.application.name"), env.getProperty("java.version"), IPUtils.getHostIp(), env.getProperty("server.port"), env.getProperty("server.servlet.context-path", ""),env.getProperty("spring.cloud.nacos.discovery.server-addr",""),env.getProperty("spring.profiles.active","default"));
    }

}
