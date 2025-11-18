package cn.xianyum.framwork.config;

import cn.xianyum.framwork.security.filter.AuthenticationTokenFilter;
import cn.xianyum.framwork.security.handler.AuthenticationEntryPointImpl;
import cn.xianyum.framwork.security.handler.LogoutSuccessHandlerImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatchers;
import org.springframework.web.filter.CorsFilter;

import java.util.stream.Collectors;

/**
 * Spring Security 配置（适配 Spring Security 6.x）
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private AuthenticationEntryPointImpl unauthorizedHandler;

    @Resource
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Resource
    private AuthenticationTokenFilter authenticationTokenFilter;

    @Resource
    private PermitAllUrlProperties permitAllUrlConfig;

    @Resource
    private CorsFilter corsFilter;


    /**
     * 核心过滤器链配置（完全移除 AntPathRequestMatcher）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 请求授权规则（使用 RequestMatchers 替代 AntPathRequestMatcher）
                .authorizeHttpRequests(auth -> auth
                        // 匿名URL（从配置文件读取，直接传入字符串路径，内部自动转换为匹配器）
                        .requestMatchers(permitAllUrlConfig.getUrls().toArray(new String[0])).permitAll()

                        // 静态资源放行（指定 HTTP 方法 + 路径）
                        .requestMatchers(HttpMethod.GET,
                                "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js",
                                "/profile/**", "/oss/getImage"
                        ).permitAll()

                        // 文档、监控、登录等接口放行
                        .requestMatchers(
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/*/api-docs",
                                "/actuator/**",
                                "/druid/**",
                                "/login",
                                "/captcha/*"
                        ).permitAll()

                        // 其余请求必须认证
                        .anyRequest().authenticated()
                )

                // 2. 禁用CSRF（JWT场景）
                .csrf(csrf -> csrf.disable())

                // 3. 认证异常处理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // 4. 无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. 退出配置（使用 RequestMatchers 定义退出路径）
                .logout(logout -> logout
                        .logoutRequestMatcher(RequestMatchers.pathMatchers(POST, "/logout")) // 明确 POST 请求退出
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                )

                // 6. 禁用iframe限制
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        // 7. 过滤器顺序配置
        http.addFilterBefore(corsFilter, LogoutFilter.class);
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * 认证管理器（6.x 标准配置）
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }


    /**
     * 密码编码器
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
