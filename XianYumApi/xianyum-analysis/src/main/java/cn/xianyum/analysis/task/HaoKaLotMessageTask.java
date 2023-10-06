package cn.xianyum.analysis.task;

import cn.xianyum.analysis.service.HaoKaLotService;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.service.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 *
 * 172号卡系统通知
 * @author zhangwei
 * @date 2023/10/6 18:29
 */
@JobHandler("haoKaLotMessageTask")
public class HaoKaLotMessageTask implements IJobHandler {

    @Autowired
    HaoKaLotService haoKaLotService;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        return haoKaLotService.pushMessage(jobMapParams,tool);
    }
}
