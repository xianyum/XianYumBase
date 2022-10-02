package cn.xianyum.sheduler.handler;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.sheduler.common.utils.JobInvokeUtil;
import java.util.Map;

/**
 * 定时任务处理（允许并发执行）
 * @author zhangwei
 * @date 2022/5/12 22:00
 */
public class QuartzJobExecution extends AbstractQuartzJob{

    @Override
    protected ReturnT doExecute(String jobHandler, Map<String, String> mapParam, SchedulerTool tool) throws Exception {
        return JobInvokeUtil.invokeJobHandler(jobHandler).execute(mapParam,tool);
    }
}
