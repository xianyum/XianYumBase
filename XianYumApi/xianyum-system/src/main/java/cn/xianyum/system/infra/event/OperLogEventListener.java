package cn.xianyum.system.infra.event;

import cn.hutool.core.bean.BeanUtil;
import cn.xianyum.common.entity.operlog.OperLogEvent;
import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.service.OperLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志事件监听器
 */
@Slf4j
@Component
public class OperLogEventListener {

    @Resource
    private OperLogService operLogService;

    @Async("xianYumTaskExecutor")
    @EventListener
    public void handleOperLogEvent(OperLogEvent event) {
        try {
            operLogService.saveOperLog(BeanUtil.copyProperties(event, OperLogEntity.class));
        } catch (Exception e) {
            log.error("操作日志保存失败", e);
        }
    }
}
