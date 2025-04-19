package cn.xianyum.message.infra.core;

import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.service.MessageMonitorService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息发送抽象类
 * @author zhangwei
 * @date 2025/4/19 21:10
 */
public abstract class AbstractMessageSender {

    @Autowired
    protected MessageMonitorService messageMonitorService;

    /**
     * 真正发送消息逻辑
     * @param messageSender
     */
    public abstract void doSendMessage(MessageSenderEntity messageSender);


    /**
     * 返回message key 用于工厂类
     * @see cn.xianyum.message.enums.MessageAccountTypeEnums
     * @return
     */
    public abstract String getMessageAccountTypeCode();
}
