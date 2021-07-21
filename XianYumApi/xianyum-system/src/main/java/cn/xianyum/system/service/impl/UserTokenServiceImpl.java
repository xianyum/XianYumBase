package cn.xianyum.system.service.impl;

import cn.xianyum.common.utils.*;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson.JSONObject;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    /**
     * 获取当前登录user
     * @return
     */
    @Override
    public UserEntity getUser(){
        String token = HttpContextUtils.getRequestToken();
        String userEntityJson = (String)redisUtils.get(prefix+token);
        UserEntity userEntity = JSONObject.parseObject(userEntityJson, UserEntity.class);
        return userEntity;
    }


    @Override
    public DataResult createToken(UserEntity user) {
        //生成token
        String token = UUIDUtils.UUIDReplace();
        Date now = new Date();

        /** 增加浏览器标识以及登录ip等 */
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
        String ip = IPUtils.getIpAddr(httpServletRequest);
        user.setIpaddr(ip);
        user.setLoginLocation(IPUtils.getIpInfo(ip));
        user.setBrowser(userAgent.getBrowser().getName());
        user.setOs(userAgent.getOperatingSystem().getName());
        user.setLoginTime(now);
        user.setLoginSystem("本站(BaseDemo)");

        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);
        UserEntity userEntity = BeanUtils.copy(user,UserEntity.class);
        redisUtils.setMin(prefix+token,JSONObject.toJSONString(userEntity),expire);
        DataResult result = DataResult.success().put("token", token).put("expire", expireTime);
        return result;
    }

    @Override
    public void logout() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String token = request.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(StringUtil.isEmpty(token)){
            token = request.getParameter("token");
        }
        redisUtils.del(prefix+token);
    }

    @Override
    public void refreshUser() {
        String token = HttpContextUtils.getRequestToken();
        UserEntity userEntity = userMapper.selectById(SecurityUtils.getLoginUser().getId());
        redisUtils.setMin(prefix+token,userEntity,expire);
    }
}
