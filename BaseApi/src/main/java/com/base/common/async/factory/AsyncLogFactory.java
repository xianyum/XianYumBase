package com.base.common.async.factory;

import com.base.common.utils.SpringUtils;
import com.base.entity.po.LogEntity;
import com.base.service.iservice.LogService;
import lombok.extern.slf4j.Slf4j;
import java.util.TimerTask;

/**
 * @author zhangwei
 * @date 2019/12/10 10:51
 */
@Slf4j
public class AsyncLogFactory {

    public static TimerTask save(final LogEntity logEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(LogService.class).saveLog(logEntity);
            }
        };
    }
}
