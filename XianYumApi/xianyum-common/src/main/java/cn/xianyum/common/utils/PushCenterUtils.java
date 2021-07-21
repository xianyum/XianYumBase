package cn.xianyum.common.utils;

import cn.xianyum.common.service.WxCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Iterator;
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

        // 构建发送markdown内容
        StringBuilder markdownStr = new StringBuilder();
        markdownStr.append("#### ");
        markdownStr.append(pushTitle);

        // 构建发送微信内容
        StringBuilder wxStr = new StringBuilder();

        // 增加消息唯一ID
        var MID = UUIDUtils.UUIDReplace();
        content.put("MID：",MID.substring(0,20)+"...");
        Iterator<Map.Entry<String, Object>> entries = content.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            markdownStr.append("\n");
            markdownStr.append(">");
            markdownStr.append("- ");
            markdownStr.append(entry.getKey());
            markdownStr.append(entry.getValue());

            wxStr.append(entry.getKey());
            wxStr.append(entry.getValue());
            wxStr.append("\n");
        }

        DingDingPushUtils.push(pushTitle,markdownStr.toString());
        wxCenterService.pushWxMessage(pushTitle,wxStr.toString());
        return MID;
    }
}
