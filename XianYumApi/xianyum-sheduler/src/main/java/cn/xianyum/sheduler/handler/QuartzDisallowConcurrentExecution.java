package cn.xianyum.sheduler.handler;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.sheduler.common.utils.JobInvokeUtil;
import org.quartz.DisallowConcurrentExecution;
import java.util.Map;

/**
 * 定时任务处理（禁止并发执行）
 * @author zhangwei
 * @date 2022/5/12 21:57
 */
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob{

    @Override
    protected ReturnT doExecute(String jobHandler, Map<String, String> mapParam, SchedulerTool tool) throws Exception {
        return JobInvokeUtil.invokeJobHandler(jobHandler).execute(mapParam,tool);
    }
}
