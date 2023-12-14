package cn.xianyum.common.utils;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.Objects;

/**
 * 安全服务工具类
 * @author zhangwei
 * @date 2021/7/16 23:29
 */
public class SecurityUtils {


    /**
     * 获取当前登录用户信息
     * @return
     */
    public static LoginUser getLoginUser(){
        // 有可能返回anonymousUser
        Authentication authentication = getAuthentication();
        if(Objects.isNull(authentication)){
            return null;
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof LoginUser){
            return (LoginUser)principal;
        }
        return null;
    }

    /**
     * 设置登录用户信息
     * @return
     */
    public static void setLoginUser(LoginUser loginUser){
        if(Objects.nonNull(loginUser)){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    /**
     * 判断是不是超级管理员
     * @return
     */
    public static boolean isSupperAdminAuth(){
        LoginUser userEntity = getLoginUser();
        return (Objects.nonNull(userEntity) && (Constants.USER_ADMIN_ACCOUNT.equals(userEntity.getUsername())
                || userEntity.getRoles().stream().anyMatch(item -> Constants.USER_ADMIN_ACCOUNT.equals(item))));
    }


    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
