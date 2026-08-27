package cn.xianyum.extension.infra.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.extension.service.EvTripService;
import jakarta.annotation.Resource;

import java.util.Map;

/**
 * @author xianyum
 * @date 2026/8/27 21:18
 */
@JobHandler("summaryEvTripTask")
public class SummaryEvTripTask implements IJobHandler {

    @Resource
    private EvTripService evTripService;

    @Override
    public ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception {
        return evTripService.doSummaryEvTrip();
    }
}
