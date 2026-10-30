package cn.xianyum.system.infra.event;

import cn.hutool.core.util.StrUtil;

/**
 * 用户角色缓存事件
 * @param userId
 */
public record UserRoleCacheChangeEvent(String userId) {

    public UserRoleCacheChangeEvent {
        if (StrUtil.isBlank(userId)) {
            throw new IllegalArgumentException("userId不能为空");
        }
    }
}
