package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.supporter.EmailSupporter;
import cn.xianyum.message.service.MessageConfigEmailService;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageTypeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * 邮箱发送
 * @author zhangwei
 * @date 2021/11/21 14:42
 */
@Component
public class EmailSender {

    @Autowired
    private EmailSupporter emailSupporter;

    @Autowired
    private MessageConfigEmailService messageConfigEmailService;

    @Autowired
    private MessageMonitorService messageMonitorService;

    @Autowired
    private MessageTypeConfigService messageTypeConfigService;

    /**
     * 发送邮件消息
     * @param messageSender
     */
    public void sendMessage(MessageSenderEntity messageSender) {
        MessageConfigEmailEntity messageConfigEmailEntity = this.messageConfigEmailService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if (messageConfigEmailEntity != null){
            if(StringUtil.isNotEmpty(messageSender.getEmailToUser())){
                String[] receivers = messageSender.getEmailToUser().split("\\|");
                for(String receiver : receivers){
                    String mId = UUIDUtils.UUIDReplace();
                    messageSender.setMessageId(mId);
                    String messageContent  = emailSupporter.generateMessageContent(messageSender);
                    messageSender.setEmailToUser(receiver);
                    String result = emailSupporter.sendHtmlContent(messageConfigEmailEntity,receiver,messageSender.getTitle(),messageContent);
                    this.messageMonitorService.insertMessageLog(mId,messageSender.getEmailToUser(),messageSender,result);
                }
            }
        }
    }

    /**
     * 指定发送邮件消息
     * @param messageSender
     */
    public void sendEmail(MessageSenderEntity messageSender) {
        messageSender.setMessageAccountType(MessageAccountTypeEnums.EMAIL.getCode());
        MessageTypeConfigEntity messageTypeConfigEntity = messageTypeConfigService.check(messageSender.getMessageCode());
        this.sendMessage(messageSender);
    }

    /**
     * 发送带有模板的消息
     * @param messageSender
     * @param context
     */
    public void sendEmailTemplateMessage(MessageSenderEntity messageSender, Context context) {
        MessageConfigEmailEntity messageConfigEmailEntity = this.messageConfigEmailService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if (messageConfigEmailEntity != null){
            if(StringUtil.isNotEmpty(messageSender.getEmailToUser())){
                String[] receivers = messageSender.getEmailToUser().split("\\|");
                for(String receiver : receivers){
                    String mId = UUIDUtils.UUIDReplace();
                    messageSender.setEmailToUser(receiver);
                    String result = emailSupporter.sendHtmlEmail(messageConfigEmailEntity,receiver,messageSender.getTitle(),messageSender.getEmailHtmlPath(),context);
                    this.messageMonitorService.insertMessageLog(mId,messageSender.getEmailToUser(),messageSender,result);
                }
            }
        }
    }
}
