package cn.xianyum.mqtt.infra.handler;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;

/**
 * MQTT拦截器抽象基础类：空实现所有方法，避免强制实现不需要的接口方法
 * @author xianyum
 * @date 2026/1/4 20:01
 */
public abstract class AbstractMqttInterceptHandler implements InterceptHandler {

    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        // 只拦截发布消息事件，减少性能消耗
        return new Class[]{InterceptPublishMessage.class};
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {}

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {}

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {}

    @Override
    public void onPublish(InterceptPublishMessage msg) {}

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {}

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {}

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {}

    @Override
    public void onSessionLoopError(Throwable throwable) {
    }
}
