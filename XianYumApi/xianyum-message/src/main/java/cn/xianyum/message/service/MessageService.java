package cn.xianyum.message.service;

import cn.xianyum.message.entity.request.FnOsPushMessageRequest;
import cn.xianyum.message.entity.request.GrafanaAlertWebhookRequest;
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

    /**
     * 接收飞牛推送的消息
     * @param request
     */
    void receiveFnOsPushMessage(FnOsPushMessageRequest request);

    /**
     * 接受GrafanaAlert发送消息
     * @param request
     */
    void receiveGrafanaAlert(GrafanaAlertWebhookRequest request);
}
