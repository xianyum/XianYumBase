package cn.xianyum.proxy.common.utils;

import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.common.utils.PushCenterUtils;
import cn.xianyum.proxy.common.factory.ProxyAsyncPushLogFactory;
import cn.xianyum.proxy.entity.po.ProxyPushLogEntity;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2021/7/18 17:30
 */
@Component
public class PushProxyUtils  extends PushCenterUtils {

    @Override
    public String push(Map<String, Object> content, String pushTitle) {

        String MID = super.push(content, pushTitle);

        //异步保存推送日志
        ProxyPushLogEntity pushLogEntity = new ProxyPushLogEntity();
        pushLogEntity.setId(MID);
        pushLogEntity.setCreateTime(new Date());
        pushLogEntity.setTitle(pushTitle);
        pushLogEntity.setContent(JSONObject.toJSONString(content));
        AsyncManager.async().execute(ProxyAsyncPushLogFactory.savePushLog(pushLogEntity));
        return MID;
    }
}
