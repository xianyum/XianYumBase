package cn.xianyum.system.common.factory;



import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.service.SystemConstantService;

import java.util.TimerTask;

/**
 * @author zhangwei
 * @date 2020/12/25 10:44
 */
public class AsyncConstantFactory {

    public static TimerTask setSystemConstantToRedis(final String keyOrId, final SystemConstantEntity systemConstantEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(SystemConstantService.class).setSystemConstantToRedis(keyOrId,systemConstantEntity);
            }
        };
    }
}
