package cn.xianyum.mqtt.config;

import io.moquette.broker.security.IAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

/**
 * MQTT客户端认证器：实现账号密码校验
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttAuthenticator implements IAuthenticator {

    /** 认证配置 */
    private final MqttConfigProperties mqttConfigProperties;

    /**
     * 核心认证方法：校验客户端连接的账号密码
     * @param clientId 客户端ID
     * @param userName 客户端传入的用户名
     * @param passwordBytes 客户端传入的密码（字节数组）
     * @return true=认证通过，false=认证失败
     */
    @Override
    public boolean checkValid(String clientId, String userName, byte[] passwordBytes) {
        // 1. 如果未开启认证，直接放行（适配新字段名：authEnabled）
        if (!mqttConfigProperties.isEnabled()) {
            log.debug("MQTT认证未开启，客户端{}直接放行", clientId);
            return true;
        }

        // 2. 空值校验：用户名或密码为空则认证失败
        if (userName == null || passwordBytes == null || passwordBytes.length == 0) {
            log.warn("MQTT客户端{}认证失败：用户名或密码为空", clientId);
            return false;
        }

        // 3. 转换密码字节数组为字符串
        String password = new String(passwordBytes, StandardCharsets.UTF_8);

        // 4. todo 后期连接数据库实现
        boolean authSuccess = mqttConfigProperties.getUsername().equals(userName)
                && mqttConfigProperties.getPassword().equals(password);
        // 5. 日志记录
        if (authSuccess) {
            log.info("MQTT客户端{}认证成功，用户名：{}", clientId, userName);
        } else {
            log.warn("MQTT客户端{}认证失败：用户名或密码错误，传入用户名：{}", clientId, userName);
        }
        return authSuccess;
    }
}
