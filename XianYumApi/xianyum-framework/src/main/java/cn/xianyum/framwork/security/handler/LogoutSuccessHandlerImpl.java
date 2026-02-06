package cn.xianyum.framwork.security.handler;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.service.LogService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2021/7/15 20:36
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private LogService logService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 保存退出日志
        LogEntity log = new LogEntity();
        log.setMethod("logout");
        log.setOperation("用户退出操作");
        LoginUser tokenUserByCache = SpringUtils.getBean(UserTokenService.class).getLoginUserByHttpRequest();
        if(Objects.nonNull(tokenUserByCache)){
            log.setUsername(tokenUserByCache.getUsername());
        }else{
            log.setUsername("none");
        }
        logService.saveLog(log);
        SpringUtils.getBean(UserTokenService.class).logout();
        HttpContextUtils.renderString(response, JSONObject.toJSONString(Results.success("退出成功")));
    }
}
