package com.base.common.async.factory;

import com.base.common.utils.SpringUtils;
import com.base.entity.po.SystemConstantEntity;
import com.base.service.iservice.SystemConstantService;
import java.util.TimerTask;

/**
 * @author zhangwei
 * @date 2020/12/25 10:44
 */
public class AsyncSystemConstantFactory {

    public static TimerTask setSystemConstantToRedis(final String keyOrId, final SystemConstantEntity systemConstantEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(SystemConstantService.class).setSystemConstantToRedis(keyOrId,systemConstantEntity);
            }
        };
    }
}
