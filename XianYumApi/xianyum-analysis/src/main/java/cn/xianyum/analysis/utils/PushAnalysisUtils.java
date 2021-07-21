package cn.xianyum.analysis.utils;

import cn.xianyum.analysis.entity.po.AnalysisPushLogEntity;
import cn.xianyum.analysis.factory.AnalysisAsyncPushLogFactory;
import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.common.utils.PushCenterUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2021/7/17 15:34
 */
@Component
public class PushAnalysisUtils extends PushCenterUtils {

    @Override
    public String push(Map<String, Object> content, String pushTitle) {

        String MID = super.push(content, pushTitle);

        //异步保存推送日志
        AnalysisPushLogEntity pushLogEntity = new AnalysisPushLogEntity();
        pushLogEntity.setId(MID);
        pushLogEntity.setCreateTime(new Date());
        pushLogEntity.setTitle(pushTitle);
        pushLogEntity.setContent(JSONObject.toJSONString(content));
        AsyncManager.async().execute(AnalysisAsyncPushLogFactory.savePushLog(pushLogEntity));
        return MID;
    }
}
