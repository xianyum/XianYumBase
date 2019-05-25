package com.base.service.impl;

import com.base.common.utils.DataResult;
import com.base.common.utils.HttpContextUtils;
import com.base.common.utils.RedisUtils;
import com.base.common.utils.StringUtil;
import com.base.config.oauth2.TokenGenerator;
import com.base.entity.po.UserEntity;
import com.base.service.iservice.UserTokenService;
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

    @Override
    public DataResult createToken(UserEntity user) {
        //生成token
        String token = TokenGenerator.generateValue();
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);
        redisUtils.setMin(prefix+token,user,expire);
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
}
