package cn.xianyum.common.enums;

import cn.xianyum.common.constant.Constants;

/**
 * @author xianyum
 * @date 2026/4/14 21:00
 */
public enum RedisKeyEnum {

    // ======================== xianyum-system模块 ========================
    USER_DATA("xianyum-system:user"),
    ANALYSIS_HAO_KAO_LOT_TOKEN("xianyum-analysis:hao_kao_lot:access_token:"),
    ANALYSIS_HAO_KAO_LOT_ARTICLE_INDEX("xianyum-analysis:hao_kao_lot:article_index"),
    BAIDU_AI_ACCESS_TOKEN("xianyum-system:baidu-ai:"),
    TOKEN_QR_TICKET("xianyum-system:login:qr-ticket:%s"),
    TOKEN_PREFIX("xianyum-system:token:"),
    TOKEN_CREDENTIALS_PREFIX("xianyum-system:credentials:%s"),
    SYSTEM_CONSTANT_PREFIX("xianyum-system:constant:"),
    DICT_TYPE_PREFIX("xianyum-system:dict_type:"),
    LOG_COUNT("xianyum-system:log_count:"),
    FILE_PRESIGNED_URL("xianyum-system:xianyum-system:file_url:%s"),
    MENU_CLICK_KEY("xianyum-system:xianyum-system:menu:click:%s:%s"),
    MENU_CLICK_RANK("xianyum-system:xianyum-system:menu:rank:%s"),


    // ======================== xianyum-message模块 ========================
    MESSAGE_WECHAT_TOKEN("xianyum-message:wechat:"),
    MESSAGE_CONFIG_PREFIX("xianyum-message:config:"),

    // ======================== xianyum-proxy模块 ========================
    PROXY_PROXY_LOG_IGNORE_SAVE("xianyum-proxy:proxy_log:ignore_save:flag"),
    PROXY_PROXY_LOG_LOG_DATA("xianyum-proxy:proxy_log:log_data:"),
    PROXY_PROXY_DETAILS_LAN_INFO("xianyum-proxy:proxy_details:lan_info:"),

    // ======================== xianyum-mqtt模块 ========================
    MQTT_FISH_LATEST_DATA("xianyum-mqtt:fish:latest"),
    MQTT_FISH_PREVIOUS_DATA("xianyum-mqtt:fish:previous");


    private final String key;

    RedisKeyEnum(String key) {
        this.key = key;
    }

    /**
     * 拼接完整 Redis Key（带应用名前缀）
     */
    public String getKey() {
        return Constants.DEFAULT_REDIS_KEY_PREFIX + this.key;
    }

    /**
     * 带参数格式化 Key（自动拼接应用名）
     */
    public String formatKey(Object... args) {
        return String.format(Constants.DEFAULT_REDIS_KEY_PREFIX + this.key, args);
    }
}
