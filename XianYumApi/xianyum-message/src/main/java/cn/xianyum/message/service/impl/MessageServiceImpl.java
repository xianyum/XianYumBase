package cn.xianyum.message.service.impl;

import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.message.entity.po.MessageContent;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageSenderRequest;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wei.zhang@onecontract-cloud.com
 * @description
 * @date 2022/1/3 18:58
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageSender messageSender;

    private static final String MESSAGE_CONTENT_PREFIX = "";
    /**
     * 发送简单消息
     *
     * @param messageCode
     * @param title
     * @param content
     */
    @Override
    public void sendSimpleMessage(String messageCode, String title, String content) {
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setTitle(title);
        List<MessageContent> messageContents = new ArrayList<>();
        MessageContent messageContent = new MessageContent();
        messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
        messageContent.setValue(content);
        messageContents.add(messageContent);
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(messageCode,messageSenderEntity);
    }

    /**
     * 发送标准消息
     * @param messageSenderRequest
     */
    @Override
    public void sendStandardMessage(MessageSenderRequest messageSenderRequest) {
        MessageSenderEntity messageSenderEntity = BeanUtils.copy(messageSenderRequest, MessageSenderEntity.class);
        List<MessageContent> messageContents = new ArrayList<>();
        MessageContent messageContent = new MessageContent();
        messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
        messageContent.setValue(messageSenderRequest.getContent());
        messageContents.add(messageContent);
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(messageSenderRequest.getMessageCode(),messageSenderEntity);
    }

}
