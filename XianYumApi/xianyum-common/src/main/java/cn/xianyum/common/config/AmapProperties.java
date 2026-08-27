package cn.xianyum.common.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置类
 * @author xianyum
 * @date 2026/8/27 23:04
 */
@Data
@Component
@ConfigurationProperties(prefix = "amap")
public class AmapProperties {

    /** 高德接口基础域名，例：https://restapi.amap.com */
    private String baseUrl;

    /** 高德Web服务key，需在开放平台申请，配置服务器出口IP白名单 */
    private String key;

    /** 逆地理编码接口路径，例：/v3/geocode/regeo */
    private String regeoPath;

}
