package cn.xianyum.common.handler;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2022/5/12 21:27
 */
public interface IJobHandler {

    ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception;
}
