package cn.xianyum.message.infra.sender;

import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.service.MessageSendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    @Autowired
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

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
        xianYumTaskExecutor.execute(()-> messageSendConfigService.sendMessage(messageCode,messageSender));
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
        xianYumTaskExecutor.execute(()-> messageSendConfigService.sendEmailTemplateMessage(messageCode,messageSender,context));
    }

}
