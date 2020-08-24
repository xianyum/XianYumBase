package com.base.service.impl;

import com.anji.captcha.service.CaptchaCacheService;
import com.base.common.utils.RedisUtils;
import com.google.auto.service.AutoService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，比如用Redis，参考service/spring-boot代码示例。
 * 如果应用是单点的，也没有使用redis，那默认使用内存。
 * 内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败。
 * @author zhangwei
 * @date 2020/8/24 15:19
 */
@AutoService(CaptchaCacheService.class)
@Slf4j
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService{

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void set(String s, String s1, long l) {
        redisUtils.set(s,s1,l);
    }

    @Override
    public boolean exists(String s) {
        return redisUtils.hasKey(s);
    }

    @Override
    public void delete(String s) {
        redisUtils.del(s);
    }

    @Override
    public String get(String s) {
        return (String)redisUtils.get(s);
    }

    @Override
    public String type() {
        return "redis";
    }
}
