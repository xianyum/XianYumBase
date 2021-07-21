package cn.xianyum.system.common.utils;

import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.common.utils.PushCenterUtils;
import cn.xianyum.system.common.factory.AsyncPushLogFactory;
import cn.xianyum.system.entity.po.PushLogEntity;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2021/7/17 15:34
 */
@Component
public class PushCenterSystemUtils extends PushCenterUtils {

    @Override
    public String push(Map<String, Object> content, String pushTitle) {

        String MID = super.push(content, pushTitle);

        //异步保存推送日志
        PushLogEntity pushLogEntity = new PushLogEntity();
        pushLogEntity.setId(MID);
        pushLogEntity.setCreateTime(new Date());
        pushLogEntity.setTitle(pushTitle);
        pushLogEntity.setContent(JSONObject.toJSONString(content));
        AsyncManager.async().execute(AsyncPushLogFactory.savePushLog(pushLogEntity));
        return MID;
    }
}
