package cn.xianyum.system.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 初始化系统用户到Redis hash表中
 * @author zhangwei
 * @date 2023/11/6 21:06
 */
@JobHandler("userToRedisTask")
public class userToRedisTask implements IJobHandler {

    @Autowired
    private UserService userService;

    @Override
    public ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception {
        int count = userService.userToRedis(false);
        return ReturnT.SUCCESS;
    }
}
