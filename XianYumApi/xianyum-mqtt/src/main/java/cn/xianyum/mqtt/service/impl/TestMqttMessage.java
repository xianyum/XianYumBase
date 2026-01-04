package cn.xianyum.mqtt.service.impl;


import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.mqtt.infra.handler.MqttMessageHandler;
import com.alibaba.fastjson2.JSONObject;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author xianyum
 * @date 2026/1/4 20:29
 */
@Service
@Slf4j
public class TestMqttMessage implements MqttMessageHandler {
    @Override
    public String supportTopic() {
        return "/data/test";
    }

    @Override
    public void handle(InterceptPublishMessage msg) {
        // 1. 获取消息体（核心：字节数组转字符串，默认UTF-8编码）
        ByteBuf payloadBuff = msg.getPayload();// 获取payload字节数组
        String payload = StringUtil.byteBuf2Utf8String(payloadBuff); // 转UTF-8字符串

        JSONObject payloadJson = JSONObject.parseObject(payload);

        // 2. 同时获取其他关键信息（便于排查/业务处理）
        String clientId = msg.getClientID(); // 发送消息的客户端ID
        String topic = msg.getTopicName(); // 消息发布的主题
        int qos = msg.getQos().ordinal(); // QoS等级（0/1/2）
        boolean retain = msg.isRetainFlag(); // 是否是保留消息

        // 3. 打印完整信息（可读性强）
        log.info("MQTT接收到消息：\n" +
                        "  客户端ID：{}\n" +
                        "  主题：{}\n" +
                        "  QoS：{}\n" +
                        "  保留消息：{}\n" +
                        "  消息体：{}",
                clientId, topic, qos, retain, payloadJson);
    }
}
