package cn.xianyum.message.infra.sender;

import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.dao.MessageSendConfigMapper;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.core.MessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 发送消息客户端入口
 * @author zhangwei
 * @date 2021/11/21 15:29
 */
@Component
public class MessageSender {

    @Autowired
    protected MessageSendConfigMapper messageSendConfigMapper;

    @Autowired
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private EmailSender emailSender;

    /**
     * 同步发送消息
     * @param messageCode
     * @param messageSender
     */
    public void sendMessage(String messageCode, MessageSenderEntity messageSender){
        List<MessageSendConfigResponse> messageSendConfigList = this.getMessageSendConfig(messageCode,messageSender);
        for(MessageSendConfigResponse messageSendConfigResponse : messageSendConfigList) {
            for(MessageSendRelationResponse item : messageSendConfigResponse.getMessageSendRelationResponses()){
                this.messageFactory.getMessageService(item.getMessageAccountType()).doSendMessage(messageSender);
            }
        }
    }

    /**
     * 异步发送消息
     * @param messageCode
     * @param messageSender
     */
    public void sendAsyncMessage(String messageCode, MessageSenderEntity messageSender){
        xianYumTaskExecutor.execute(()-> this.sendMessage(messageCode,messageSender));
    }


    /**
     * 异步发送email模板邮件
     * @param messageCode
     * @param messageSender
     * @param context
     */
    public void sendAsyncEmailTemplateMessage(String messageCode, MessageSenderEntity messageSender,Context context){
        xianYumTaskExecutor.execute(()-> {
            List<MessageSendConfigResponse> messageSendConfigList = this.getMessageSendConfig(messageCode,messageSender);
            for(MessageSendConfigResponse messageSendConfigResponse : messageSendConfigList) {
                for(MessageSendRelationResponse item : messageSendConfigResponse.getMessageSendRelationResponses()){
                    // 发送邮箱消息
                    switch (MessageAccountTypeEnums.getByCode(item.getMessageAccountType())){
                        case EMAIL:
                            emailSender.sendEmailTemplateMessage(messageSender,context);
                            break;
                    }
                }
            }
        });
    }

    /**
     * 获取发送消息配置类,并校验消息配置
     * @return
     */
    public List<MessageSendConfigResponse> getMessageSendConfig(String messageCode, MessageSenderEntity messageSender){
        List<MessageSendConfigResponse> messageSendConfigResponseList = messageSendConfigMapper.selectListByMessageCode(messageCode);
        List<MessageSendConfigResponse> resultList = new ArrayList<>();
        for (MessageSendConfigResponse messageSendConfigResponse : messageSendConfigResponseList) {
            if(verifySendRules(messageSendConfigResponse)){
                continue;
            }
            if(StringUtil.isEmpty(messageSender.getTitle())){
                messageSender.setTitle(messageSendConfigResponse.getMessageTitle());
            }
            messageSender.setMessageCode(messageSendConfigResponse.getMessageCode());
            for(MessageSendRelationResponse item : messageSendConfigResponse.getMessageSendRelationResponses()){
                messageSender.setMessageConfigId(item.getMessageConfigId());
                messageSender.setMessageAccountType(item.getMessageAccountType());
                messageSender.setDefaultToUser(item.getToUser());
            }
            resultList.add(messageSendConfigResponse);
        }
        return resultList;
    }

    /**
     * 校验发信规则
     * @param messageSendConfigResponse
     * @return
     */
    public boolean verifySendRules(MessageSendConfigResponse messageSendConfigResponse) {
        if(Objects.isNull(messageSendConfigResponse)){
            return true;
        }
        if(null != messageSendConfigResponse.getLimitSendStartTime() && null != messageSendConfigResponse.getLimitSendEndTime()){
            Date limitSendStartTime = messageSendConfigResponse.getLimitSendStartTime();
            Date limitSendEndTime = messageSendConfigResponse.getLimitSendEndTime();
            if(DateUtils.checkNowBetweenTime(limitSendStartTime,limitSendEndTime)){
                return true;
            }
        }
        return false;
    }


}
