package cn.xianyum.system.infra.event;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

public record RoleMenuCacheChangeEvent(Set<String> roleCodes) {

    public RoleMenuCacheChangeEvent {
        if (CollUtil.isEmpty(roleCodes)) {
            throw new IllegalArgumentException("roleCodes不能为空");
        }
    }
}
