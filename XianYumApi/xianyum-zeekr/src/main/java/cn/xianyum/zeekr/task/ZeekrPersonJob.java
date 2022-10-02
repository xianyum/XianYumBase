package cn.xianyum.zeekr.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.service.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.zeekr.service.ZeekrPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author zhangwei
 * @date 2022/8/7 14:21
 */
@Slf4j
@JobHandler("zeekrPersonTsJob")
public class ZeekrPersonJob implements IJobHandler {


    @Autowired
    private ZeekrPersonService zeekrPersonService;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        return zeekrPersonService.executeZeekr(jobMapParams,tool);
    }
}
