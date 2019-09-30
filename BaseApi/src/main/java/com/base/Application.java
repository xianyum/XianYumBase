package com.base;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class}) //去掉springboot 默认的数据源配置
@MapperScan("com.base.dao")
@EnableScheduling
@EnableSwagger2Doc
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

