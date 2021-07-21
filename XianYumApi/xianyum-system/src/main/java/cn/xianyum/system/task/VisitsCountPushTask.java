package cn.xianyum.system.task;

import cn.xianyum.system.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 访问次数推送
 * @author zhangwei
 * @date 2020/8/21 10:40
 */
@Component
@Slf4j
public class VisitsCountPushTask {

    @Autowired
    private LogService logService;

    @Scheduled(cron = "0 0 9,14,18,21,23 * * ?")  //每隔1小时执行一次
    public void pushMessage() {
        logService.push();
    }
}
