package cn.xianyum.starter;


import cn.xianyum.proxy.container.ProxyServerContainer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class}) //去掉springboot 默认的数据源配置
@MapperScan("cn.xianyum.**.dao")
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = {"cn.xianyum"})
public class XianyumApplication {

    public static void main(String[] args) {
        SpringApplication.run(XianyumApplication.class, args);
        ProxyServerContainer proxyServerContainer = new ProxyServerContainer();
        proxyServerContainer.start();
        System.out.println("测试自动化部署2");
    }

}
