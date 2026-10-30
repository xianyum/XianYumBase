package cn.xianyum.system.infra.event;

import cn.hutool.core.collection.CollUtil;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class RoleMenuCacheChangeEventListener {

    @Resource
    private RedisUtils redisUtils;

    @Async
    @EventListener
    public void handleRoleMenuCacheChange(RoleMenuCacheChangeEvent event) {
        Set<String> roleCodes = event.roleCodes();
        if (CollUtil.isEmpty(roleCodes)) {
            return;
        }
        redisUtils.hDel(RedisKeyEnum.MENU_PERMISSION_BY_ROLE_KEY.getKey(), roleCodes.toArray());
    }
}
