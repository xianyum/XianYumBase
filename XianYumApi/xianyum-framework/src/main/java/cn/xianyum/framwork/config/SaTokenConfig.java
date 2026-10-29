package cn.xianyum.framwork.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.xianyum.common.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
            @Override
            public String wrapKey(String key) {
                return Constants.DEFAULT_REDIS_KEY_PREFIX + key;
            }
        };
    }
}
