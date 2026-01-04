package cn.xianyum.mqtt.config;

import cn.xianyum.mqtt.infra.handler.MqttMessageHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xianyum
 * @date 2026/1/4 19:54
 */
@Slf4j
@Component
public class MqttTopicRouterConfig {

    // 注入所有实现MqttMessageHandler接口的Bean
    private final List<MqttMessageHandler> messageHandlers;

    // 缓存Topic与处理器的映射（线程安全）
    private final Map<String, MqttMessageHandler> handlerMap = new ConcurrentHashMap<>();

    // 使用AntPathMatcher匹配MQTT通配符（兼容/、+、#）
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public MqttTopicRouterConfig(List<MqttMessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    /**
     * 初始化：将所有处理器注册到Map中
     */
    @PostConstruct
    public void init() {
        for (MqttMessageHandler handler : messageHandlers) {
            String topic = handler.supportTopic();
            handlerMap.put(topic, handler);
            log.info("注册MQTT处理器 -> Topic: {}, 处理器: {}", topic, handler.getClass().getSimpleName());
        }
    }

    /**
     * 根据消息Topic匹配对应的处理器
     * @param msg MQTT消息
     */
    public void route(InterceptPublishMessage msg) {
        String topic = msg.getTopicName();
        // 1. 查找匹配的处理器
        Optional<MqttMessageHandler> matchedHandler = findMatchedHandler(topic);

        // 2. 处理消息（找到处理器则执行，否则走默认逻辑）
        if (matchedHandler.isPresent()) {
            matchedHandler.get().handle(msg);
        } else {
            log.warn("未找到匹配的处理器，使用默认逻辑处理 -> Topic: {}", topic);
            handleDefault(msg);
        }
    }

    /**
     * 匹配Topic对应的处理器（支持通配符）
     * @param topic 消息Topic
     * @return 匹配的处理器
     */
    private Optional<MqttMessageHandler> findMatchedHandler(String topic) {
        // 先精确匹配
        if (handlerMap.containsKey(topic)) {
            return Optional.of(handlerMap.get(topic));
        }

        // 再匹配通配符（比如device/# 匹配 device/data/temperature）
        for (Map.Entry<String, MqttMessageHandler> entry : handlerMap.entrySet()) {
            String pattern = entry.getKey();
            // 替换MQTT通配符为AntPathMatcher格式：+ -> *，# -> **
            String antPattern = pattern.replace("+", "*").replace("#", "**");
            if (pathMatcher.match(antPattern, topic)) {
                return Optional.of(entry.getValue());
            }
        }

        // 无匹配处理器
        return Optional.empty();
    }

    /**
     * 默认处理逻辑：未匹配到专属处理器时执行
     * @param msg MQTT消息
     */
    private void handleDefault(InterceptPublishMessage msg) {
        String clientId = msg.getClientID();
        String topic = msg.getTopicName();
        String payload = new String(msg.getPayload().array());
        log.info("[默认处理器] 处理消息 -> clientId: {}, topic: {}, payload: {}", clientId, topic, payload);
        // 可添加默认保存逻辑（比如统一存入通用表）
    }
}
