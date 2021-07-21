package cn.xianyum.system.common.factory;


import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.service.LogService;
import java.util.TimerTask;

/**
 * @author zhangwei
 * @date 2019/12/10 10:51
 */
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
