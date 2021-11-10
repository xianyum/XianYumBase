package cn.xianyum.common.service.impl;

import cn.xianyum.common.service.WxCenterService;
import cn.xianyum.common.utils.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

/**
 * 微信中心
 * @author zhangwei
 * @date 2019/9/29 13:57
 */
@Slf4j
@Service
public class WxCenterServiceImpl implements WxCenterService {

    @Value("${push.wx.corpid:xxxxxxx}")
    private String corpId;

    @Value("${push.wx.corpsecret:xxxxxxx}")
    private String corpSecret;

    @Value("${push.wx.toUser:xxxxxxx}")
    private String toUser;

    @Value("${push.wx.agentId:xxxxxxx}")
    private String agentId;
    /**
     * 发送企业微信通知
     * 参考：https://work.weixin.qq.com/api/doc/90000/90135/90236#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF
     * @param pushTitle
     * @param pushContent
     */
    @Override
    public void pushWxMessage(String pushTitle, Map<String, Object> pushContent) {

        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+this.getWxToken();

        // 构建发送微信内容
        StringBuilder wxStr = new StringBuilder();
        Iterator<Map.Entry<String, Object>> entries = pushContent.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            wxStr.append(entry.getKey());
            wxStr.append(entry.getValue());
            wxStr.append("\n");
        }

        String content = pushTitle+"\n"+wxStr.toString();
        JSONObject requestJson = new JSONObject();
        requestJson.put("touser",toUser);
        requestJson.put("msgtype","text");
        requestJson.put("agentid",agentId);
        JSONObject textJson = new JSONObject();
        textJson.put("content",content);
        requestJson.put("text",textJson);

        HttpUtils.getHttpInstance().sync(sendUrl)
                .bodyType(OkHttps.JSON).setBodyPara(requestJson).post().close();
    }

    @Override
    public String getWxToken() {
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpId+"&corpsecret="+corpSecret;
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(sendUrl).get();
        if(httpResult.isSuccessful()){
            JSONObject responseObject = JSONObject.parseObject(httpResult.getBody().toString());
            return responseObject.getString("access_token");
        }
        return "";
    }

}
