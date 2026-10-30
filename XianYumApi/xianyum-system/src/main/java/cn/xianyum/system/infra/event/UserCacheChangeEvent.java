package cn.xianyum.system.infra.event;

import cn.hutool.core.util.StrUtil;

/**
 * 用户缓存事件
 * @param userId
 */
public record UserCacheChangeEvent(String userId) {

    public UserCacheChangeEvent {
        if (StrUtil.isBlank(userId)) {
            throw new IllegalArgumentException("userId不能为空");
        }
    }
}
