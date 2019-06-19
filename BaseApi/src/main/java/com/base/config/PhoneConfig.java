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
@ConfigurationProperties(prefix = "tencent.phone")
public class PhoneConfig {
    /**
     * 短信应用SDK AppID
     */
    private int appid;

    /**
     *  短信应用SDK AppKey
     */
    private String appkey;
    /**
     * 短信模板ID
     */
    private int templateId;
    /**
     * 签名（可为空）
     */
    private String smsSign;

}
