package cn.xianyum.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xianyum
 * @date 2026/1/4 20:04
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfigProperties {
    /** 监听端口 */
    private String port;
    /** 默认0.0.0.0所有都能访问 */
    private String host;
    /** 是否开启账号密码认证 */
    private boolean enabled = false;
    /** 默认用户名 */
    private String username;
    /** 默认密码 */
    private String password;
}
