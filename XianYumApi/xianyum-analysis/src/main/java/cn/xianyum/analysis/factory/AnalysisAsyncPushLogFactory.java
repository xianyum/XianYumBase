package cn.xianyum.analysis.factory;


import cn.xianyum.analysis.entity.po.AnalysisPushLogEntity;
import cn.xianyum.analysis.service.AnalysisPushLogService;
import cn.xianyum.common.utils.SpringUtils;
import java.util.TimerTask;

/**
 * 异步保存推送的消息
 * @author zhangwei
 * @date 2021/7/17 19:38
 */
public class AnalysisAsyncPushLogFactory {

    public static TimerTask savePushLog(final AnalysisPushLogEntity pushLogEntity){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(AnalysisPushLogService.class).insert(pushLogEntity);
            }
        };
    }
}
