package cn.xianyum.framwork.satoken.interceptor;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;


/**
 * @author xianyum
 * @date 2026/10/31 22:20
 */
@Slf4j
public class UserContextInterceptor extends SaInterceptor {

    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     * @param auth
     */
    public UserContextInterceptor(SaParamFunction<Object> auth) {
        super(auth);
    }

    /**
     * 每次请求之前触发的方法
     *
     * @param request
     * @param response
     * @param handler
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean checkPass = super.preHandle(request, response, handler);
        if (!checkPass) {
            return false;
        }
        // 设置traceId
        MDC.put(Constants.TRACE_ID, IdUtil.fastSimpleUUID());
        // 设置上下文用户信息
        if(StpUtil.isLogin()){
            UserContextHolder.setLoginUser(StpUtil.getLoginIdAsString(),StpUtil.getTokenValue());
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        UserContextHolder.clear();
        MDC.remove(Constants.TRACE_ID);
    }
}
