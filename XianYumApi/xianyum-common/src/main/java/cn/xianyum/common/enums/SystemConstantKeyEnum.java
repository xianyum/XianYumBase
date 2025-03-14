package cn.xianyum.common.enums;

/**
 * @author zhangwei
 * @date 2025/3/14 20:43
 */
public enum SystemConstantKeyEnum {

    SYSTEM("system","系统设置"),
    XIAN_YU_CLIENT("xianyu_client","获取咸鱼客户端信息"),
    BAIDU_AI_OCR("baidu_ai_ocr","百度ai开放平台ocr识别"),
    HAO_KA_LOT_LOGIN_INFO("hao_ka_lot_login_info","172号卡平台登录账号和密码"),
    GOLD_CONFIG("gold_config","金价接口配置"),
    HOLIDAY_CN("holiday_cn","中国法定节假日")
    ;

    private String key;
    private String desc;

    SystemConstantKeyEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }
}
