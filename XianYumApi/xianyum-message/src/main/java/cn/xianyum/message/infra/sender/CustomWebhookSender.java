package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.core.AbstractMessageSender;
import cn.xianyum.message.infra.supporter.WebhookSupporter;
import cn.xianyum.message.service.MessageConfigWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2025/4/19 23:12
 */
@Component
public class CustomWebhookSender extends AbstractMessageSender {

    @Autowired
    private WebhookSupporter webhookSupporter;

    @Autowired
    private MessageConfigWebhookService messageConfigWebhookService;

    /**
     * 真正发送消息逻辑
     *
     * @param messageSender
     */
    @Override
    public void doSendMessage(MessageSenderEntity messageSender) {
        MessageConfigWebhookEntity webhookConfig = messageConfigWebhookService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(webhookConfig != null){
            String mId = UUIDUtils.UUIDReplace();
            messageSender.setMessageId(mId);
            String sendResult = webhookSupporter.sendCustomMessage(webhookConfig,messageSender);
            this.messageMonitorService.insertMessageLog(mId,null,messageSender,sendResult);
        }
    }

    /**
     * 返回message key 用于工厂类
     *
     * @return
     * @see MessageAccountTypeEnums
     */
    @Override
    public String getMessageAccountTypeCode() {
        return MessageAccountTypeEnums.CUSTOM_WEBHOOK.getCode();
    }
}
