package com.base.config;

import com.base.config.oauth2.OAuth2Filter;
import com.base.config.oauth2.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/2/2 11:24
 */
@Configuration
public class ShiroConfig {

    @Value("${redis.token.enable}")
    private boolean validateToken;

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuth2Realm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/systemConstant/getPublicConstant", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/getIpInfo", "anon");
        filterMap.put("/captcha/get", "anon");
        filterMap.put("/captcha/check", "anon");
        filterMap.put("/oss/upload", "anon");
        filterMap.put("/oss/getImage", "anon");
        filterMap.put("/gitee/push", "anon");
        filterMap.put("/ali/login", "anon");
        filterMap.put("/qq/login", "anon");
        filterMap.put("/wxCenter/get", "anon");
        filterMap.put("/loginPhone", "anon");
        filterMap.put("/getRegisterCode", "anon");
        filterMap.put("/getPhoneCode", "anon");
        filterMap.put("/register", "anon");
        filterMap.put("/caos/export", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/captcha.jpg", "anon");
        if(validateToken){
            filterMap.put("/**", "oauth2");
        }
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    /**
     *  加入static修饰，为了解决整合shiro时，@value注解无法读取application.yml中的配置
     *  不清楚会发生其他错误
     */
    @Bean("lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
