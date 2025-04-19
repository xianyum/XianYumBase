package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.core.AbstractMessageSender;
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
public class WechatSender extends AbstractMessageSender {

    @Autowired
    private WechatSupporter wechatSupporter;

    @Autowired
    private MessageConfigWechatService messageConfigWechatService;


    @Autowired
    private MessageTypeConfigService messageTypeConfigService;


    /**
     * 真正发送消息逻辑
     *
     * @param messageSender
     */
    @Override
    public void doSendMessage(MessageSenderEntity messageSender) {
        // 接口指定的发送用户>发送配置的>默认all
        messageSender.setWechatToUser(StringUtil.isNotEmpty(messageSender.getWechatToUser())?messageSender.getWechatToUser():messageSender.getDefaultToUser());
        MessageConfigWechatEntity messageConfigWechatEntity = messageConfigWechatService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if(messageConfigWechatEntity != null && messageSender != null ){
            if(StringUtil.isEmpty(messageSender.getWechatToUser())){
                // 企微如果没有指定发送人的话，默认发全部
                messageSender.setWechatToUser("@all");
            }
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
        messageTypeConfigService.check(messageSender.getMessageCode());
        this.doSendMessage(messageSender);
    }


    /**
     * 返回message key 用于工厂类
     *
     * @return
     * @see MessageAccountTypeEnums
     */
    @Override
    public String getMessageAccountTypeCode() {
        return MessageAccountTypeEnums.WECHAT.getCode();
    }
}
