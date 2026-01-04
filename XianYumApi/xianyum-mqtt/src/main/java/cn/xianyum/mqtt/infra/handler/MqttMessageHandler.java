package cn.xianyum.mqtt.infra.handler;

import io.moquette.interception.messages.InterceptPublishMessage;

/**
 * MQTT消息处理器接口，不同Topic对应不同实现类
 * @author xianyum
 * @date 2026/1/4 19:47
 */
public interface MqttMessageHandler {

    /**
     * 获取当前处理器支持的Topic（支持通配符，如device/data/temperature、device/data/+）
     * @return 支持的Topic表达式
     */
    String supportTopic();

    /**
     * 处理MQTT消息
     * @param msg MQTT发布消息对象
     */
    void handle(InterceptPublishMessage msg);
}
