package cn.xianyum.analysis.infra.task;

import cn.xianyum.analysis.service.XiaoDaoService;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2022/5/14 23:57
 */
@JobHandler("xiaoDaoPushMessageTask")
public class XiaoDaoPushMessageTask implements IJobHandler {

    @Autowired
    private XiaoDaoService xiaoDaoService;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        xiaoDaoService.push();
        return ReturnT.SUCCESS;
    }
}
