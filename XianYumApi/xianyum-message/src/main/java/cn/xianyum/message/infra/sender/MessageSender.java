package cn.xianyum.message.infra.sender;

import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.infra.factory.MessageSenderFactory;
import cn.xianyum.message.service.MessageSendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * 发送消息客户端入口
 * @author zhangwei
 * @date 2021/11/21 15:29
 */
@Component
public class MessageSender {

    @Autowired
    private MessageSendConfigService messageSendConfigService;

    /**
     * 同步发送消息
     * @param messageCode
     * @param messageSender
     */
    public void sendMessage(String messageCode, MessageSenderEntity messageSender){
        messageSendConfigService.sendMessage(messageCode,messageSender);
    }

    /**
     * 异步发送消息
     * @param messageCode
     * @param messageSender
     */
    public void sendAsyncMessage(String messageCode, MessageSenderEntity messageSender){
        AsyncManager.async().execute(MessageSenderFactory.sendMessage(messageCode,messageSender));
    }

    /**
     * 同步发送email模板邮件
     * @param messageCode
     * @param messageSender
     * @param context
     */
    public void sendEmailTemplateMessage(String messageCode, MessageSenderEntity messageSender,Context context) {
        messageSendConfigService.sendEmailTemplateMessage(messageCode,messageSender,context);
    }

    /**
     * 异步发送email模板邮件
     * @param messageCode
     * @param messageSender
     * @param context
     */
    public void sendAsyncEmailTemplateMessage(String messageCode, MessageSenderEntity messageSender,Context context){
        AsyncManager.async().execute(MessageSenderFactory.sendEmailTemplateMessage(messageCode,messageSender,context));
    }

}
