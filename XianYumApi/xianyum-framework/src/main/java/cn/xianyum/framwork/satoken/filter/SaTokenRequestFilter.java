package cn.xianyum.framwork.satoken.filter;

import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.stp.StpUtil;
import cn.xianyum.framwork.satoken.interceptor.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;
import java.util.Objects;

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
                    // 标注 @SaCheckApiKey 的接口走 ApiKey 鉴权，无需校验登录态
                    if (isApiKeyRequest(handler)) {
                        return;
                    }
                    StpUtil.checkLogin();
                })).addPathPatterns("/**")
                .excludePathPatterns("/v3/api-docs/**","/error","/swagger-ui.html", "/swagger-ui/**", "/druid/**","/actuator/**");
    }

    /**
     * 判断当前请求是否为 ApiKey 鉴权接口（方法或其所在类上标注了 @SaCheckApiKey）
     * @param handler 请求处理器
     * @return true-是
     */
    private boolean isApiKeyRequest(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        Method method = handlerMethod.getMethod();
        return Objects.nonNull(AnnotationUtils.findAnnotation(method, SaCheckApiKey.class))
                || Objects.nonNull(AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), SaCheckApiKey.class));
    }
}
