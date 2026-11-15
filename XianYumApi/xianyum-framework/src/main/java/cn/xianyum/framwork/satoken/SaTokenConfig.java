package cn.xianyum.framwork.satoken;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.xianyum.common.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.types.Expiration;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token配置
 * @author xianyum
 * @date 2026/10/29 20:43
 */
@Configuration
public class SaTokenConfig {

    @Bean
    @Primary
    public SaTokenDao saTokenDao() {

        return new SaTokenDaoForRedisTemplate() {

            /**
             * 包装 key（默认原样返回）。需要给 Redis 键加统一前缀时，可重写此方法。
             *
             * @param key 原始 key
             * @return 包装后的 key
             */
            @Override
            public String wrapKey(String key) {
                return Constants.DEFAULT_REDIS_KEY_PREFIX + key;
            }

            /**
             * 修改指定key-value键值对 (过期时间不变)
             *
             * <p> 使用 Redis SET KEEPTTL（{@link Expiration#keepTtl()}），要求 Redis 版本 >= 6.0。
             * 低于 6.0 的兼容写法见本类文件底部注释。
             *
             * @param key
             * @param value
             */
            @Override
            public void update(String key, String value) {
                String finalKey = wrapKey(key);
                long expireMs = stringRedisTemplate.getExpire(finalKey, TimeUnit.MILLISECONDS);
                // -2 = 无此键
                if (expireMs == SaTokenDao.NOT_VALUE_EXPIRE) {
                    return;
                }
                // -1 = 永不过期
                if (expireMs == SaTokenDao.NEVER_EXPIRE) {
                    stringRedisTemplate.opsForValue().set(finalKey, value);
                } else {
                    stringRedisTemplate.opsForValue().set(finalKey, value, expireMs, TimeUnit.MILLISECONDS);
                }
            }
        };
    }
}
