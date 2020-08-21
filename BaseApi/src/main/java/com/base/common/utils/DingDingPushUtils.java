package com.base.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipaySecurityProdCtidVerifyModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import java.net.URLEncoder;

/**
 * @author zhangwei
 * @date 2020/8/21 13:35
 */
@Slf4j
public class DingDingPushUtils {

    private static final String SECRET = "SEC9e92d51cbd1766a9d2ccdb8dfbebd7862e04a46923c5d6dec950dd19c3333a9c";
    private static final String URL = "https://oapi.dingtalk.com/robot/send?access_token=f6a9684a35620efe0ac41f7aa5d99f70858c71646b867340dae19f52d25b6fc9";

    public static void push(String title,String text){

        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + SECRET;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            String url = URL +"&timestamp="+timestamp+"&sign="+sign;

            JSONObject requestJson = new JSONObject();
            requestJson.put("msgtype","markdown");
            JSONObject markdown = new JSONObject();
            markdown.put("title",title);
            markdown.put("text",text);
            requestJson.put("markdown",markdown);
            HttpUtils.sendPostJson(url,requestJson.toJSONString());
        }catch (Exception e){
            log.error("send ding ding message error.",e);
        }
    }

    public static void main(String[] args) {

        StringBuilder markdownStr = new StringBuilder();
        markdownStr.append("#### ");
        markdownStr.append("Photoshop CC 2019精简");
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("[https://www.xd0.com/i-wz-56060507.html](https://www.xd0.com/i-wz-56060507.html)");
        push("11",markdownStr.toString());

    }
}
