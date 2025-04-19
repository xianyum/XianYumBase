package cn.xianyum.message.infra.sender;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.core.AbstractMessageSender;
import cn.xianyum.message.infra.supporter.EmailSupporter;
import cn.xianyum.message.service.MessageConfigEmailService;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageTypeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import java.util.List;

/**
 * 邮箱发送
 * @author zhangwei
 * @date 2021/11/21 14:42
 */
@Component
public class EmailSender extends AbstractMessageSender {

    @Autowired
    private EmailSupporter emailSupporter;

    @Autowired
    private MessageConfigEmailService messageConfigEmailService;

    @Autowired
    private MessageMonitorService messageMonitorService;

    @Autowired
    private MessageTypeConfigService messageTypeConfigService;

    /**
     * 真正发送消息逻辑
     *
     * @param messageSender
     */
    @Override
    public void doSendMessage(MessageSenderEntity messageSender) {
        messageSender.setEmailToUser(StringUtil.isNotEmpty(messageSender.getEmailToUser())?messageSender.getEmailToUser():messageSender.getDefaultToUser());
        MessageConfigEmailEntity messageConfigEmailEntity = this.messageConfigEmailService.getMessageConfigWithCache(messageSender.getMessageConfigId());
        if (messageConfigEmailEntity != null){
            if(StringUtil.isNotEmpty(messageSender.getEmailToUser())){
                String[] receivers = messageSender.getEmailToUser().split("\\|");
                for(String receiver : receivers){
                    String mId = UUIDUtils.UUIDReplace();
                    messageSender.setMessageId(mId);
                    List<String> messageContentList  = emailSupporter.generateMessageContentList(messageSender);
                    messageSender.setEmailToUser(receiver);
                    Context context = new Context();
                    context.setVariable("messageTitle", messageSender.getTitle());
                    context.setVariable("messageContentList", messageContentList);
                    String result = emailSupporter.sendHtmlEmail(messageConfigEmailEntity,receiver,messageSender.getTitle(), Constants.DEFAULT_EMAIL_HTML,context);
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
        messageTypeConfigService.check(messageSender.getMessageCode());
        this.doSendMessage(messageSender);
    }

    /**
     * 发送带有模板的消息
     * @param messageSender
     * @param context
     */
    public void sendEmailTemplateMessage(MessageSenderEntity messageSender, Context context) {
        messageSender.setEmailToUser(StringUtil.isNotEmpty(messageSender.getEmailToUser())?messageSender.getWechatToUser():messageSender.getDefaultToUser());
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


    /**
     * 返回message key 用于工厂类
     *
     * @return
     * @see MessageAccountTypeEnums
     */
    @Override
    public String getMessageAccountTypeCode() {
        return MessageAccountTypeEnums.EMAIL.getCode();
    }
}
