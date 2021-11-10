package cn.xianyum.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.OkHttps;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2020/8/21 13:35
 */
@Slf4j
public class DingDingPushUtils {

    private static final String SECRET = PropertiesUtil.getString("push.dingding.secret");
    private static final String URL = PropertiesUtil.getString("push.dingding.url");

    public static void push(String title, Map<String, Object> content){

        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + SECRET;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)),"UTF-8");
            String url = URL +"&timestamp="+timestamp+"&sign="+sign;

            // 构建发送消息体
            StringBuilder markdownStr = new StringBuilder();
            markdownStr.append("#### ");
            markdownStr.append(title);
            Iterator<Map.Entry<String, Object>> entries = content.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                markdownStr.append("\n");
                markdownStr.append(">");
                markdownStr.append("- ");
                markdownStr.append(entry.getKey());
                markdownStr.append(entry.getValue());
            }

            // 发送消息
            JSONObject requestJson = new JSONObject();
            requestJson.put("msgtype","markdown");
            JSONObject markdown = new JSONObject();
            markdown.put("title",title);
            markdown.put("text",markdownStr.toString());
            requestJson.put("markdown",markdown);
            HttpUtils.getHttpInstance().sync(url)
                    .bodyType(OkHttps.JSON).setBodyPara(requestJson).post().close();
        }catch (Exception e){
            log.error("send ding ding message error.",e);
        }
    }

    public static void main(String[] args) {

        StringBuilder markdownStr = new StringBuilder();
        markdownStr.append("#### ");
        markdownStr.append("Photoshop CC 2019精简111");
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("[https://www.xd0.com/i-wz-56060507.html](https://www.xd0.com/i-wz-56060507.html)");
    }
}
