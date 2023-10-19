package cn.xianyum.system.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.system.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * 访问次数推送
 * @author zhangwei
 * @date 2020/8/21 10:40
 */
@JobHandler("visitsCountPushTask")
public class VisitsCountPushTask implements IJobHandler {

    @Autowired
    private LogService logService;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        logService.push();
        return ReturnT.SUCCESS;
    }
}
