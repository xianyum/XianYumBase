package cn.xianyum.framwork.satoken.filter;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.xianyum.framwork.satoken.interceptor.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
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
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 校验登录状态
                    StpUtil.checkLogin();
                })).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE).excludePathPatterns("/v3/api-docs/**","/error","/swagger-ui.html", "/swagger-ui/**", "/druid/**","/actuator/**");
        registry.addInterceptor(new UserContextInterceptor()).order(1).addPathPatterns("/**");
    }
}
