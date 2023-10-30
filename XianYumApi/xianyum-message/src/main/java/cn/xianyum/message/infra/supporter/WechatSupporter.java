package cn.xianyum.message.infra.supporter;

import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.po.MessageContent;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.po.WxTextCardMessage;
import cn.xianyum.message.infra.utils.MessageUtils;
import com.alibaba.fastjson2.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 企业微信支持类
 * @author zhangwei
 * @date 2021/11/21 17:02
 */
@Component
public class WechatSupporter {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.message.wechat.token:xianyum-message}")
    private String accessTokenPrefix;

    /**
     * 获取企业微信token
     * @param messageConfigWechatEntity
     * @return
     */
    public String getWxTokenWithCache(MessageConfigWechatEntity messageConfigWechatEntity){
        String cropId = messageConfigWechatEntity.getCorpId();
        String cropSecret = messageConfigWechatEntity.getCorpSecret();
        String redisKey = accessTokenPrefix+cropId+cropSecret;
        if(redisUtils.hasKey(redisKey)){
            return (String) redisUtils.get(redisKey);
        }
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+cropId+"&corpsecret="+cropSecret;
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(sendUrl).get();
        if(httpResult.isSuccessful()){
            JSONObject responseObject = JSONObject.parseObject(httpResult.getBody().toString());
            String access_token = responseObject.getString("access_token");
            redisUtils.set(redisKey,access_token);
            redisUtils.expire(redisKey,6000);
            return access_token;
        }
        return "";
    }

    public String sendTextCard(MessageConfigWechatEntity messageConfigWechatEntity, String messageJson){

        String accessToken = this.getWxTokenWithCache(messageConfigWechatEntity);
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+accessToken;
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(sendUrl)
                .bodyType(OkHttps.JSON).setBodyPara(messageJson).post();
        return httpResult.getBody().toString();
    }


    public String generateMessage(String agentId,MessageSenderEntity messageSenderEntity){

        StringBuffer sb = new StringBuffer();
        if(StringUtil.isNotEmpty(messageSenderEntity.getContent())){
            sb.append(messageSenderEntity.getContent());
        }else{
            List<MessageContent> messageContents = messageSenderEntity.getMessageContents();
            if(messageContents != null && messageContents.size() > 0){
                for(MessageContent item : messageContents){
                    sb.append("<div>");
                    sb.append(item.getLabel());
                    sb.append(item.getValue());
                    sb.append("</div>");
                }
            }
        }

        WxTextCardMessage.TextCard card = new WxTextCardMessage.TextCard();
        card.setDescription(sb.toString());
        card.setUrl(MessageUtils.getFormUrl(messageSenderEntity.getMessageId()));
        card.setTitle(messageSenderEntity.getTitle());
        card.setBtntxt("详情");
        WxTextCardMessage message = new WxTextCardMessage(agentId);
        message.setTextcard(card);
        message.setTouser(messageSenderEntity.getWechatToUser());
        return JSONObject.toJSONString(message);
    }

}
