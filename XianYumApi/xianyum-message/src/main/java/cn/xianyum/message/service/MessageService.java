package cn.xianyum.message.service;

import cn.xianyum.message.entity.request.MessageSenderRequest;

/**
 *
 * @description
 * @date 2022/1/3 18:58
 */
public interface MessageService {
    /**
     * 发送简单消息
     * @param messageCode
     * @param title
     * @param content
     */
    void sendSimpleMessage(String messageCode, String title, String content);

    /**
     * 发送标准消息
     * @param messageSenderRequest
     */
    void sendStandardMessage(MessageSenderRequest messageSenderRequest);
}
