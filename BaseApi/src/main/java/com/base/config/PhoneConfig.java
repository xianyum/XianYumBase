package com.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2019/3/27 15:40
 */
@Data
@Component
@ConfigurationProperties(prefix = "phone")
public class PhoneConfig {

    private String sid;
    private String token;
    private String appid;
    private String templateid;

}
