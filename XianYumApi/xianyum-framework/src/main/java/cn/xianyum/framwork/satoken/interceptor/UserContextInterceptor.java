package cn.xianyum.framwork.satoken.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author xianyum
 * @date 2026/10/30 22:21
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("执行顺序2");
        if(StpUtil.isLogin()){
            String loginId = StpUtil.getLoginIdAsString();
            UserContextHolder.setLoginUser(loginId);
        }
        MDC.put(Constants.TRACE_ID, IdUtil.fastSimpleUUID());
        return true;
    }

    /**
     * 无论正常返回还是抛出异常，都会执行，清理ThreadLocal
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.clear();
        MDC.remove(Constants.TRACE_ID);
    }

}
