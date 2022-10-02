package cn.xianyum.message.infra.factory;

import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageSendConfigService;
import org.thymeleaf.context.Context;

import java.util.TimerTask;

/**
 * @author
 * @description
 * @date 2021/11/24 17:01
 */
public class MessageSenderFactory {

    public static TimerTask sendMessage(final String messageCode,final MessageSenderEntity messageSender){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(MessageSendConfigService.class).sendMessage(messageCode,messageSender);
            }
        };
    }


    public static TimerTask sendEmailTemplateMessage(final String messageCode,final MessageSenderEntity messageSender,final Context context){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(MessageSendConfigService.class).sendEmailTemplateMessage(messageCode,messageSender,context);
            }
        };
    }

    public static TimerTask insertMessageLog(final String mId,final String toUser,final MessageSenderEntity messageSender,final String sendResult){
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(MessageMonitorService.class).insertMessageLog(mId,toUser,messageSender,sendResult);
            }
        };
    }
}
