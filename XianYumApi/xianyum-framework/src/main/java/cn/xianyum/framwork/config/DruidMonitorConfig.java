//package cn.xianyum.framwork.config;
//
//import com.alibaba.druid.support.jakarta.StatViewServlet;
//import com.alibaba.druid.support.jakarta.WebStatFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author xianyum
// * @date 2025/11/19 23:07
// */
//@Configuration
//public class DruidMonitorConfig {
//
//    // 注册监控 Servlet（访问：http://localhost:8080/druid）
//    @Bean
//    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
//        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
//        registrationBean.addInitParameter("loginUsername", "admin"); // 监控页面登录账号
//        registrationBean.addInitParameter("loginPassword", "123456"); // 登录密码
//        registrationBean.addInitParameter("allow", ""); // 允许所有IP访问（生产环境限制IP）
//        return registrationBean;
//    }
//
//    // 注册监控 Filter（统计 Web 请求）
//    @Bean
//    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter() {
//        FilterRegistrationBean<WebStatFilter> registrationBean = new FilterRegistrationBean<>(new WebStatFilter());
//        registrationBean.addUrlPatterns("/*"); // 监控所有请求
//        registrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.css,/druid/*"); // 排除静态资源和监控页面
//        return registrationBean;
//    }
//}
