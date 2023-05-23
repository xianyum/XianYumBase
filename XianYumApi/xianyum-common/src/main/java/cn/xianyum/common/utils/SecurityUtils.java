package cn.xianyum.common.utils;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.exception.SoException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 * @author zhangwei
 * @date 2021/7/16 23:29
 */
public class SecurityUtils {

    private static final String admin = "admin";

    /**
     * 获取当前登录用户信息
     * @return
     */
    public static LoginUser getLoginUser(){
        // 有可能返回anonymousUser
        Object principal = getAuthentication().getPrincipal();
        if(principal instanceof LoginUser){
            return (LoginUser)getAuthentication().getPrincipal();
        }else{
            return null;
        }
    }


    /**
     * 只能允许admin操作
     */
    public static void allowAdminAuth(){
        LoginUser userEntity = getLoginUser();
        if(userEntity == null ||
                (!admin.equals(userEntity.getUsername())  || userEntity.getPermission() != PermissionEnum.ADMIN.getStatus())){
            throw new SoException(HttpStatus.FORBIDDEN.value(),"对不起，您没有权限操作,请联系网站管理员！");
        }
    }


    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
