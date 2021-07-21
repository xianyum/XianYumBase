package cn.xianyum.proxy.common.factory;

import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.proxy.entity.po.ProxySystemConstantEntity;
import cn.xianyum.proxy.service.ProxySystemConstantService;
import java.util.TimerTask;

/**
 * @author zhangwei
 * @date 2020/12/25 10:44
 */
public class ProxyAsyncSystemConstantFactory {

    public static TimerTask setSystemConstantToRedis(final String keyOrId, final ProxySystemConstantEntity systemConstantEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(ProxySystemConstantService.class).setSystemConstantToRedis(keyOrId,systemConstantEntity);
            }
        };
    }
}
