package cn.xianyum.message.enums;

/**
 * @author wei.zhang
 * @description
 * @date 2021/11/26 15:16
 */
public enum MessageCodeEnums {

    NEW_ACTIVITY_NOTIFY("NEW_ACTIVITY_NOTIFY","最新活动通知"),
    SYSTEM_VISITS_NOTIFY("SYSTEM_VISITS_NOTIFY","每天系统访问量通知"),
    XIANYU_ONLINE_OFFLINE_NOTIFY("XIANYU_ONLINE_OFFLINE_NOTIFY","咸鱼客户端上下线通知"),
    XIANYU_CONFIG_DETAIL_NOTIFY("XIANYU_CONFIG_DETAIL_NOTIFY","咸鱼客户端配置详情通知"),
    SYSTEM_TEST_NOTIFY("SYSTEM_TEST_NOTIFY","系统测试消息通知"),
    SYSTEM_EXCEPTION_NOTIFY("SYSTEM_EXCEPTION_NOTIFY","系统异常消息通知"),
    ZEEKR_EXECUTE_FAIL_NOTIFY("ZEEKR_EXECUTE_FAIL_NOTIFY","zeekr执行失败消息通知"),
    ALI_YUN_XIAO_NOTIFY("ALI_YUN_XIAO_NOTIFY","云效流水线执行结果通知");

    private String messageCode;
    private String description;

    MessageCodeEnums(String messageCode, String description) {
        this.messageCode = messageCode;
        this.description = description;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
