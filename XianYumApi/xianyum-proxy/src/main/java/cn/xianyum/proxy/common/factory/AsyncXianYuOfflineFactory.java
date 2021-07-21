package cn.xianyum.proxy.common.factory;


import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.proxy.service.ProxyService;
import java.util.TimerTask;

/**
 * 咸鱼客户端下线
 * @author zhangwei
 * @date 2021/6/1 17:36
 */
public class AsyncXianYuOfflineFactory {

    public static TimerTask notify(final String clientKey){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(ProxyService.class).offlineNotify(clientKey);
            }
        };
    }
}
