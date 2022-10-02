package cn.xianyum.common.service;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2022/5/12 21:27
 */
public interface IJobHandler {

    ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception;
}
