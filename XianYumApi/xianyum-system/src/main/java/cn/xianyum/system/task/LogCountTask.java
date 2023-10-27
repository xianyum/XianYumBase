package cn.xianyum.system.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.system.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * 访问次数推送
 * @author zhangwei
 * @date 2020/8/21 10:40
 */
@Slf4j
@JobHandler("logCountTask")
public class LogCountTask implements IJobHandler {

    @Autowired
    private LogService logService;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        DateTime dateTime = DateTime.now().minusDays(1);
        String s = dateTime.toString(DateUtils.DATE_PATTERN);
        int logCountWithCache = this.logService.getLogCountWithCache(s);
        return ReturnT.SUCCESS;
    }
}
