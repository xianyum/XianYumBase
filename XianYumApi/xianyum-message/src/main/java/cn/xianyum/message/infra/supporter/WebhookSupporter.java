package cn.xianyum.message.infra.supporter;


import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.MD5Utils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.po.MessageContent;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.zhxu.okhttps.HttpResult;
import cn.zhxu.okhttps.OkHttps;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 邮箱支持类
 * @author zhangwei
 * @date 2021/11/21 14:55
 */
@Component
@Slf4j
public class WebhookSupporter {


    public String sendDdMessage(MessageConfigWebhookEntity webhookConfig, MessageSenderEntity messageSender) {

        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + webhookConfig.getWebHookSecret();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookConfig.getWebHookSecret().getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)),"UTF-8");
            String url = webhookConfig.getWebHookUrl() +"&timestamp="+timestamp+"&sign="+sign;

            // 构建发送消息体
            StringBuilder markdownStr = new StringBuilder();
            markdownStr.append("#### ");
            markdownStr.append(messageSender.getTitle());

            if(StringUtil.isNotEmpty(messageSender.getContent())){
                markdownStr.append("\n");
                markdownStr.append(">");
                markdownStr.append("- ");
                markdownStr.append(messageSender.getContent());
            }else{
                List<MessageContent> messageContents = messageSender.getMessageContents();
                if(messageContents != null && messageContents.size() >0){
                    for(MessageContent item : messageContents){
                        markdownStr.append("\n");
                        markdownStr.append(">");
                        markdownStr.append("- ");
                        markdownStr.append(item.getLabel());
                        markdownStr.append(item.getValue());
                    }
                }
            }

            markdownStr.append("\n");
            markdownStr.append(">");
            markdownStr.append("- ");
            markdownStr.append("查看详情：");
            markdownStr.append("[点击查看消息详情]");
            markdownStr.append("(");
            markdownStr.append(MessageUtils.getFormUrl(messageSender.getMessageId()));
            markdownStr.append(")");

            // 发送消息
            JSONObject requestJson = new JSONObject();
            requestJson.put("msgtype","markdown");
            JSONObject markdown = new JSONObject();
            markdown.put("title",messageSender.getTitle());
            markdown.put("text",markdownStr.toString());
            requestJson.put("markdown",markdown);
            HttpResult httpResult = HttpUtils.getHttpInstance().sync(url)
                    .bodyType(OkHttps.JSON).setBodyPara(requestJson).post();
            return httpResult.getBody().toString();
        }catch (Exception e){
            log.error("send ding ding webhook message error.",e);
            return e.getMessage();
        }
    }

    public String sendFsMessage(MessageConfigWebhookEntity webhookConfig, MessageSenderEntity messageSender) {

        try {
            long timestamp = System.currentTimeMillis() / 1000;
            JSONObject requestParam = new JSONObject();
            requestParam.put("timestamp",timestamp);
            requestParam.put("sign",genFsSign(webhookConfig.getWebHookSecret(),timestamp));
            requestParam.put("msg_type","post");

            List<List<JSONObject>> contentParamList = new ArrayList<>();

            if(StringUtil.isNotEmpty(messageSender.getContent())){
                List<JSONObject> contentParam = new ArrayList<>();
                JSONObject contentBean = new JSONObject();
                contentBean.put("tag","text");
                contentBean.put("text",messageSender.getContent());
                contentParam.add(contentBean);
                contentParamList.add(contentParam);
            }else{
                List<MessageContent> messageContents = messageSender.getMessageContents();
                if(messageContents != null && messageContents.size() >0){
                    for(MessageContent item : messageContents){
                        List<JSONObject> contentParam = new ArrayList<>();
                        JSONObject contentBean = new JSONObject();
                        contentBean.put("tag","text");
                        contentBean.put("text",item.getLabel()+item.getValue());
                        contentParam.add(contentBean);
                        contentParamList.add(contentParam);
                    }

                }
            }

            List<JSONObject> urlParam = new ArrayList<>();
            JSONObject contentBean1 = new JSONObject();
            contentBean1.put("tag","text");
            contentBean1.put("text","查看详情：");
            JSONObject contentBean2 = new JSONObject();
            contentBean2.put("tag","a");
            contentBean2.put("text","点击查看消息详情");
            contentBean2.put("href",MessageUtils.getFormUrl(messageSender.getMessageId()));
            urlParam.add(contentBean1);
            urlParam.add(contentBean2);
            contentParamList.add(urlParam);

            String postJson = "{\"post\": {"+" \"zh_cn\": {\"title\":\""+messageSender.getTitle()+"\",\"content\":"+JSONObject.toJSONString(contentParamList)+"}}}";
            requestParam.put("content",JSONObject.parseObject(postJson));
            HttpResult httpResult = HttpUtils.getHttpInstance().sync(webhookConfig.getWebHookUrl()).bodyType(OkHttps.JSON).setBodyPara(requestParam).post();
            return httpResult.getBody().toString();
        }catch (Exception e){
            log.error("send fs webhook message error.",e);
            return e.getMessage();
        }
    }

    public String sendCustomMessage(MessageConfigWebhookEntity webhookConfig, MessageSenderEntity messageSender) {

        try {
            String webHookSecret = webhookConfig.getWebHookSecret();
            Long timestamp = System.currentTimeMillis();
            JSONObject requestJson = new JSONObject();
            requestJson.put("timestamp",timestamp);
            requestJson.put("sign", MD5Utils.getMd5(timestamp+webHookSecret));
            requestJson.put("data", messageSender);
            HttpResult httpResult = HttpUtils.getHttpInstance().sync(webhookConfig.getWebHookUrl())
                    .bodyType(OkHttps.JSON).setBodyPara(requestJson).post();
            return httpResult.getBody().toString();
        }catch (Exception e){
            log.error("send custom webhook message error.",e);
            return e.getMessage();
        }

    }

    public  String genFsSign(String secret, Long timestamp) {
        try {
            //把timestamp+"\n"+密钥当做签名字符串
            String stringToSign = timestamp + "\n" + secret;

            //使用HmacSHA256算法计算签名
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(new byte[]{});
            return new String(Base64.getEncoder().encode(signData));
        }catch (Exception e){
            log.error("生成飞书秘钥失败. ",e);
        }
        return null;
    }

    /**
     * 发送企微webHook消息
     * @param webhookConfig
     * @param messageSender
     * @return
     */
    public String sendWechatMessage(MessageConfigWebhookEntity webhookConfig, MessageSenderEntity messageSender) {
        try {
            // 构建发送消息体
            StringBuilder markdownStr = new StringBuilder();
            markdownStr.append("#### ");
            markdownStr.append(messageSender.getTitle());
            if(StringUtil.isNotEmpty(messageSender.getContent())){
                markdownStr.append("\n");
                markdownStr.append(">");
                markdownStr.append(messageSender.getContent());
            }else {
                List<MessageContent> messageContents = messageSender.getMessageContents();
                if(messageContents != null && messageContents.size() >0){
                    for(MessageContent item : messageContents){
                        markdownStr.append("\n");
                        markdownStr.append(">");
                        markdownStr.append(item.getLabel());
                        markdownStr.append(item.getValue());
                    }
                }
            }
            JSONObject requestObject = new JSONObject();
            requestObject.put("msgtype","markdown");
            JSONObject contentObject = new JSONObject();
            contentObject.put("content",markdownStr.toString());
            requestObject.put("markdown",contentObject);
            HttpResult httpResult = HttpUtils.getHttpInstance().sync(webhookConfig.getWebHookUrl())
                    .bodyType(OkHttps.JSON).setBodyPara(requestObject.toJSONString()).post();
            return httpResult.getBody().toString();
        }catch (Exception e){
            log.error("send wechat webhook message error.",e);
            return e.getMessage();
        }
    }
}
