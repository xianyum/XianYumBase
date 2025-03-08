package cn.xianyum.extension.infra.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.extension.service.GoldPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025/2/15 22:43
 */
@JobHandler("goldPriceTask")
public class GoldPriceTask implements IJobHandler {

    @Autowired
    private GoldPriceService goldPriceService;

    @Override
    public ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception {
        return goldPriceService.pullGoldPrice(jobParamsMap,tool);
    }

}
