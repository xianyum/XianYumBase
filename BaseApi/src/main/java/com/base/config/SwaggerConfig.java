//package com.base.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.List;
//
//import static com.google.common.collect.Lists.newArrayList;
//
///**
// * @author zhangwei
// * @date 2019/1/31 14:27
// */
//@Configuration
//@EnableSwagger2
//@ConditionalOnProperty(prefix = "swagger",value = {"enable"},havingValue = "true")
//public class SwaggerConfig implements WebMvcConfigurer {
//    @Value("${swagger.title}")
//    private String title;
//
//    @Value("${swagger.description}")
//    private String description;
//
//    @Value("${swagger.version}")
//    private String version;
//
//    @Value("${swagger.basePackage}")
//    private String basePackage;
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                //加了ApiOperation注解的类，才生成接口文档
//                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                //包下的类，才生成接口文档
//                .apis(RequestHandlerSelectors.basePackage(basePackage))
//                .paths(PathSelectors.any())
//                .build()
//                .securitySchemes(security());//这个注解是token 在右上角Author...
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title(title)
//                .description(description)
//                .version(version)
//                .build();
//    }
//
//    private List<ApiKey> security() {
//        return newArrayList(
//                new ApiKey("token", "token", "header")
//        );
//    }
//
//}
