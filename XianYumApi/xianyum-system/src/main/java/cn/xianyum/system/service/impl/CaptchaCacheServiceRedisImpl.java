package cn.xianyum.system.service.impl;

import cn.xianyum.common.utils.RedisUtils;
import com.anji.captcha.service.CaptchaCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


/**
 * 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，比如用Redis，参考service/spring-boot代码示例。
 * 如果应用是单点的，也没有使用redis，那默认使用内存。
 * 内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败。
 *
 * @Title: 使用redis缓存
 */
@Slf4j
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    @Override
    public String type() {
        return "redis";
    }

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.captcha.prefix}")
    private String redisKeyPrefix;

    @Value("${redis.captcha.expire}")
    private long expire;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        redisUtils.set(this.getCustomerKey(key), value, expire);
    }

    @Override
    public boolean exists(String key) {
        return redisUtils.hasKey(this.getCustomerKey(key));
    }

    @Override
    public void delete(String key) {
        redisUtils.del(this.getCustomerKey(key));
    }

    @Override
    public String get(String key) {
        return (String)redisUtils.get(this.getCustomerKey(key));
    }

    public String getCustomerKey(String key){
        String uuid = key.substring(key.lastIndexOf(":")+1);
        return redisKeyPrefix+uuid;
    }
}
