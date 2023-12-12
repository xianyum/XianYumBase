package cn.xianyum.framwork.config;

import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import com.alibaba.druid.util.DruidWebUtils;
import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author zhangwei
 * @date 2019/4/25 14:46
 */

@Component
public class RequestFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestFilter.class);
    private static final String PARAM_NAME_EXCLUSIONS = "exclusions";
    private Set<String> excludesPattern;
    private String contextPath;
    protected PatternMatcher pathMatcher = new ServletPathMatcher();

    public RequestFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        StopWatch watch = StopWatch.createStarted();
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (this.isExclusion(httpRequest.getRequestURI())) {
                chain.doFilter(request, response);
            } else {
                this.buildRequestId(httpRequest);
                String path = httpRequest.getRequestURI();
                String queryStr = httpRequest.getQueryString();
                if (queryStr != null) {
                    path = path + "?" + httpRequest.getQueryString();
                }
                try {
                    chain.doFilter(httpRequest, httpResponse);
                } finally {
                    log.info("access url [{}], cost time [{}] ms )", path, watch.getTime());
                    watch.stop();
                    MDC.clear();
                }

            }
        } else {
            throw new ServletException("HttpFilter can't handle an non-http request");
        }
    }

    private void buildRequestId(HttpServletRequest httpRequest) {
        String requestId = httpRequest.getHeader("requestId");
        if (StringUtil.isEmpty(requestId)) {
            requestId = UUIDUtils.UUIDReplace();
        }
        MDC.put("requestId", requestId);
        String serverIp = IPUtils.getIpAddr(httpRequest);
        MDC.put("serverIp", serverIp);
        String clientIp =IPUtils.getHostIp();
        MDC.put("clientIp", clientIp);
    }

    public boolean isExclusion(String requestURI) {
        if (this.excludesPattern == null) {
            return false;
        } else {
            if (this.contextPath != null && requestURI.startsWith(this.contextPath)) {
                requestURI = requestURI.substring(this.contextPath.length());
                if (!requestURI.startsWith("/")) {
                    requestURI = "/" + requestURI;
                }
            }

            Iterator var2 = this.excludesPattern.iterator();

            String pattern;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                pattern = (String)var2.next();
            } while(!this.pathMatcher.matches(pattern, requestURI));

            return true;
        }
    }

    public void init(FilterConfig config) throws ServletException {
        String exclusions = config.getInitParameter("exclusions");
        if (exclusions != null && exclusions.trim().length() != 0) {
            this.excludesPattern = new HashSet(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }
        this.contextPath = DruidWebUtils.getContextPath(config.getServletContext());
    }

    public void destroy() {
    }
}
