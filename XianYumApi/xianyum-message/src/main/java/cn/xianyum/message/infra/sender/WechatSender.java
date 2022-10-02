package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.supporter.WechatSupporter;
import cn.xianyum.message.service.MessageConfigWechatService;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageTypeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 企业微信发送
 * @author zhangwei
 * @date 2021/11/21 14:43
 */
@Component
@Slf4j
public class WechatSender {

    @Autowired
    private WechatSupporter wechatSupporter;

    @Autowired
    private MessageConfigWechatService messageConfigWechatService;

    @Autowired
    private MessageMonitorService messageMonitorService;

    @Autowired
    private MessageTypeConfigService messageTypeConfigService;

    public void sendMessage(MessageSenderEntity messageSender) {
        MessageConfigWechatEntity messageConfigWechatEntity = messageConfigWechatService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(messageConfigWechatEntity != null && messageSender != null && StringUtil.isNotEmpty(messageSender.getWechatToUser())){
            String mId = UUIDUtils.UUIDReplace();
            messageSender.setMessageId(mId);
            String messageContent = wechatSupporter.generateMessage(messageConfigWechatEntity.getAgentId(),messageSender);
            String sendResult = wechatSupporter.sendTextCard(messageConfigWechatEntity, messageContent);
            this.messageMonitorService.insertMessageLog(mId,messageSender.getWechatToUser(),messageSender,sendResult);
        }
    }

    /**
     * 指定发送企微消息
     * @param messageSender
     */
    public void sendWechat(MessageSenderEntity messageSender) {
        messageSender.setMessageAccountType(MessageAccountTypeEnums.WECHAT.getCode());
        MessageTypeConfigEntity messageTypeConfigEntity = messageTypeConfigService.check(messageSender.getMessageCode());
        this.sendMessage(messageSender);
    }
}
