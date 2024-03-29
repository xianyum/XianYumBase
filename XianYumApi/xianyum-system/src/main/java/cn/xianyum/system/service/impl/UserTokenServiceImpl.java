package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.MenuService;
import cn.xianyum.system.service.RoleService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSONObject;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Value("${redis.token.expire:60}")
    private Integer expire;

    @Value("${redis.token.prefix:token}")
    private String prefix;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    /**
     * 获取当前登录user
     * @return
     */
    @Override
    public LoginUser getUserSelf(){
        return SecurityUtils.getLoginUser();
    }

    @Override
    public void setExtraUserInfo(LoginUser loginUser) {
        // 增加浏览器标识以及登录ip等
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
        String ip = IPUtils.getIpAddr(httpServletRequest);
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(IPUtils.getIpInfo(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
        loginUser.setLoginTime(new Date());

        // =======设置权限===========
        this.roleService.setLoginUserRoleService(loginUser);
        // 设置菜单权限
        loginUser.setPermissions(menuService.getMenuPermission(loginUser.getId()));
    }

    /**
     * 通过httpRequest获取当前登录用户
     *
     * @return
     */
    @Override
    public LoginUser getLoginUserByHttpRequest() {
        String token = HttpContextUtils.getRequestToken();
        String userEntityJson = (String)SpringUtils.getBean(RedisUtils.class).get(prefix+token);
        LoginUser loginUser = JSONObject.parseObject(userEntityJson, LoginUser.class);
        return loginUser;
    }


    @Override
    public Results createToken(LoginUser loginUser) {
        //生成token
        String token = UUIDUtils.UUIDReplace();
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);
        // 设置额外用户信息
        this.setExtraUserInfo(loginUser);

        redisUtils.setMin(prefix+token,JSONObject.toJSONString(loginUser),expire);
        Results result = Results.success().put("token", token).put("expire", expireTime);
        return result;
    }

    @Override
    public void logout() {
        String token =  HttpContextUtils.getRequestToken();
        redisUtils.del(prefix+token);
    }

    @Override
    public void refreshUser() {
        String token = HttpContextUtils.getRequestToken();
        UserEntity userEntity = userMapper.selectById(SecurityUtils.getLoginUser().getId());

        LoginUser loginUser = BeanUtils.copy(userEntity,LoginUser.class);
        // 保留本来登录的字段，不能被覆盖。
        loginUser.setLoginType(SecurityUtils.getLoginUser().getLoginType());

        this.setExtraUserInfo(loginUser);
        redisUtils.setMin(prefix+token,JSONObject.toJSONString(loginUser),expire);
    }
}
