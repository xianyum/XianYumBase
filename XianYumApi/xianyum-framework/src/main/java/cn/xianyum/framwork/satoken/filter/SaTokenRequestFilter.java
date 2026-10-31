package cn.xianyum.framwork.satoken.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.xianyum.framwork.satoken.interceptor.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户上下文拦截器，包括sa-token认证，权限校验，用户上下文设置
 * @author xianyum
 * @date 2026/10/29 20:46
 */
@Configuration
@Slf4j
public class SaTokenRequestFilter implements WebMvcConfigurer {

    /**
     * 注册sa-token拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserContextInterceptor(handler->{
                    StpUtil.checkLogin();
                })).addPathPatterns("/**")
                .excludePathPatterns("/v3/api-docs/**","/error","/swagger-ui.html", "/swagger-ui/**", "/druid/**","/actuator/**");
    }
}
