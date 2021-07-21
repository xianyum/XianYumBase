package cn.xianyum.proxy.common.factory;

import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.proxy.entity.po.ProxyPushLogEntity;
import cn.xianyum.proxy.service.ProxyPushLogService;

import java.util.TimerTask;

/**
 * 异步保存推送的消息
 * @author zhangwei
 * @date 2021/7/17 19:38
 */
public class ProxyAsyncPushLogFactory {

    public static TimerTask savePushLog(final ProxyPushLogEntity pushLogEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(ProxyPushLogService.class).insert(pushLogEntity);
            }
        };
    }
}
