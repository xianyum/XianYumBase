package cn.xianyum.system.common.factory;



import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.PushLogEntity;
import cn.xianyum.system.service.PushLogService;

import java.util.TimerTask;

/**
 * 异步保存推送的消息
 * @author zhangwei
 * @date 2021/7/17 19:38
 */
public class AsyncPushLogFactory {

    public static TimerTask savePushLog(final PushLogEntity pushLogEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(PushLogService.class).insert(pushLogEntity);
            }
        };
    }
}
