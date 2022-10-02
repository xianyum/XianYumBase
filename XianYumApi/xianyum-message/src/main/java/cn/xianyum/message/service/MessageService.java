package cn.xianyum.message.service;

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
}
