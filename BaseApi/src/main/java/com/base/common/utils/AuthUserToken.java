package com.base.common.utils;

import com.base.entity.enums.PermissionEnum;
import com.base.entity.po.UserEntity;
import org.apache.shiro.SecurityUtils;

/**
 * 获取登录用户信息
 * @author zhangwei
 * @date 2019/4/17 15:01
 */
public class AuthUserToken {

    public static UserEntity getUser(){
        UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
        if(userEntity == null){
            userEntity = new UserEntity();
            userEntity.setId("00000");
            userEntity.setPermission(PermissionEnum.VISITOR.getStatus());
            userEntity.setUsername("system");
        }
        return userEntity;
    }

}
