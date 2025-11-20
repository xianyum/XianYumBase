package cn.xianyum.framwork.config;

import cn.xianyum.common.config.XianYumConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

/**
 * @author xianyum
 * @date 2025/11/19 23:17
 */
@Configuration
public class OpenApiConfig {

    @Value("${xian_yum.version}")
    private String version;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components()
                        // 设置认证的请求头
                        .addSecuritySchemes("apikey", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("apikey"))
                .info(getApiInfo());
    }


    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("Authorization")
                .in(SecurityScheme.In.HEADER)
                .scheme("Bearer");
    }

    /**
     * 添加摘要信息
     */
    public Info getApiInfo() {
        return new Info()
                // 设置标题
                .title("XianYumApi_接口文档")
                // 描述
                .description("XianYumApi_接口文档")
                // 作者信息
                .contact(new Contact().url(XianYumConfig.getXianYumConfig().getWebUrl())
                        .name(XianYumConfig.getXianYumConfig().getName()))
                // 版本号
                .version(version);
    }
}
