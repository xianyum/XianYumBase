package cn.xianyum.system.infra.event;

import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户缓存事件监听器
 */
@Slf4j
@Component
public class UseCacheChangeEventListener {

    @Resource
    private RedisUtils redisUtils;

    @Async
    @EventListener
    public void handleUserDataChange(UserCacheChangeEvent event) {
        String userId = event.userId();
        if (StrUtil.isBlank(userId)) {
            return;
        }
        redisUtils.hDel(RedisKeyEnum.USER_DATA.getKey(), userId);
    }
}
