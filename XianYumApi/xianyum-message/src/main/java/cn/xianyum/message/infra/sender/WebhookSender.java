package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.supporter.WebhookSupporter;
import cn.xianyum.message.service.MessageConfigWebhookService;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageTypeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * webhook发送
 * @author zhangwei
 * @date 2021/11/21 14:43
 */
@Component
@Slf4j
public class WebhookSender {

    @Autowired
    private WebhookSupporter webhookSupporter;

    @Autowired
    private MessageConfigWebhookService messageConfigWebhookService;

    @Autowired
    private MessageMonitorService messageMonitorService;

    @Autowired
    private MessageTypeConfigService messageTypeConfigService;

    public void sendDdMessage(MessageSenderEntity messageSender) {
        MessageConfigWebhookEntity webhookConfig = messageConfigWebhookService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(webhookConfig != null){
            String mId = UUIDUtils.UUIDReplace();
            messageSender.setMessageId(mId);
            String sendResult = webhookSupporter.sendDdMessage(webhookConfig, messageSender);
            this.messageMonitorService.insertMessageLog(mId,null,messageSender,sendResult);
        }
    }

    public void sendFsMessage(MessageSenderEntity messageSender) {
        MessageConfigWebhookEntity webhookConfig = messageConfigWebhookService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(webhookConfig != null){
            String mId = UUIDUtils.UUIDReplace();
            messageSender.setMessageId(mId);
            String sendResult = webhookSupporter.sendFsMessage(webhookConfig,messageSender);
            this.messageMonitorService.insertMessageLog(mId,null,messageSender,sendResult);
        }
    }

    public void sendCustomMessage(MessageSenderEntity messageSender) {
        MessageConfigWebhookEntity webhookConfig = messageConfigWebhookService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(webhookConfig != null){
            String mId = UUIDUtils.UUIDReplace();
            messageSender.setMessageId(mId);
            String sendResult = webhookSupporter.sendCustomMessage(webhookConfig,messageSender);
            this.messageMonitorService.insertMessageLog(mId,null,messageSender,sendResult);
        }
    }

    /**
     * 指定发送webhook消息
     * @param messageSender
     */
    public void sendWebhook(MessageSenderEntity messageSender) {
        MessageTypeConfigEntity messageTypeConfigEntity = messageTypeConfigService.check(messageSender.getMessageCode());
        switch (MessageAccountTypeEnums.getByCode(messageSender.getMessageAccountType())){
            case DD_WEBHOOK:
                this.sendDdMessage(messageSender);
                break;
            case FS_WEBHOOK:
                this.sendFsMessage(messageSender);
                break;
            case CUSTOM_WEBHOOK:
                this.sendCustomMessage(messageSender);
                break;
        }
    }


}
