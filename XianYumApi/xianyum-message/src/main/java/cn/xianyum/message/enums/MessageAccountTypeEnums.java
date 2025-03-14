package cn.xianyum.message.enums;

/**
 * @author zhangwei
 * @date 2021/11/21 0:07
 */
public enum MessageAccountTypeEnums {
    EMAIL("email","邮箱","email"),
    WECHAT("wechat","微信","wechat"),
    DD_WEBHOOK("dd-webhook","钉钉webhook","webhook"),
    FS_WEBHOOK("fs-webhook","飞书webhook","webhook"),
    WECHAT_WEBHOOK("wechat-webhook","企微webhook","webhook"),
    CUSTOM_WEBHOOK("custom-webhook","自定义webhook","webhook"),
    ;


    private String code;
    private String name;
    private String type;

    MessageAccountTypeEnums(String code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static MessageAccountTypeEnums getByCode(String code) {
        for (MessageAccountTypeEnums messageAccountTypeEnums : values()) {
            if (messageAccountTypeEnums.getCode().equalsIgnoreCase(code)) {
                return messageAccountTypeEnums;
            }
        }
        return CUSTOM_WEBHOOK;
    }
}
