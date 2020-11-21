package com.base.config.oauth2;

import com.base.common.utils.RedisUtils;
import com.base.dao.UserMapper;
import com.base.entity.enums.UserStatusEnum;
import com.base.entity.po.UserEntity;
import com.base.service.iservice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 认证
 *
 */
@Component
@Slf4j
public class OAuth2Realm extends AuthorizingRealm {
    @Value("${redis.token.prefix:token}")
    private String prefix;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //用户权限列表
        Set<String> permsSet = userService.getPermissions();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String accessToken = (String) token.getPrincipal();
        UserEntity userEntity = (UserEntity)redisUtils.get(prefix+accessToken);
        if(userEntity == null){
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }
        //注释掉是为了配合支付宝做第三方登录
        //查询用户信息
        //UserEntity user = userMapper.selectById(userEntity.getId());
        //账号锁定
        if(userEntity.getStatus() == UserStatusEnum.BAN.getStatus()){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userEntity, accessToken, getName());
        return info;
    }
}
