package cn.xianyum.framwork.security.handler;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.Result;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.service.LogService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2021/7/15 20:36
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private LogService logService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 保存退出日志
        LogEntity log = new LogEntity();
        log.setMethod("logout");
        log.setOperation("用户退出操作");
        LoginUser tokenUserByCache = userTokenService.getLoginUserByHttpRequest();
        if(Objects.nonNull(tokenUserByCache)){
            log.setUsername(tokenUserByCache.getUsername());
        }else{
            log.setUsername("none");
        }
        logService.saveLog(log);
        userTokenService.logout();
        HttpContextUtils.renderString(response, JSONObject.toJSONString(Result.success("退出成功")));
    }
}
