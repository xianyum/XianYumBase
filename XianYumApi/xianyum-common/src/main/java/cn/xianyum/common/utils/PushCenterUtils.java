package cn.xianyum.common.utils;

import cn.xianyum.common.service.WxCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 推送给第三方工具
 * 统一调用这个接口
 * @author zhangwei
 * @date 2021/6/3 14:29
 */
@Component
public class PushCenterUtils {

    @Autowired
    private WxCenterService wxCenterService;

    /**
     *
     * @param content 用linkedHashMap实例化，保证有序
     * @param pushTitle 推送标题
     * @return
     */
    public String push(Map<String,Object> content,String pushTitle){
        // 增加消息唯一ID
        var MID = UUIDUtils.UUIDReplace();
        content.put("MID：",MID.substring(0,20)+"...");
        DingDingPushUtils.push(pushTitle,content);
        wxCenterService.pushWxMessage(pushTitle,content);
        FeiShuPushUtils.push(pushTitle,content);
        return MID;
    }
}
