package cn.xianyum.framwork.satoken.filter;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.xianyum.common.utils.UserContextHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xianyum
 * @date 2026/10/29 20:46
 */
@Configuration
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
                    // 设置当前线程登录用户
                    UserContextHolder.setLoginUser(String.valueOf(StpUtil.getLoginId()));
                })).addPathPatterns("/**")
                .excludePathPatterns("/v3/api-docs/**","/swagger-ui.html", "/swagger-ui/**", "/druid/**","/actuator/**");
    }
}
