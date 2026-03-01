package cn.xianyum.common.enums;

/**
 * 扫码登录状态枚举
 * @author xianyum
 * @date 2026/2/28 20:17
 */
public enum QrCodeLoginStatus {

    UNSCANNED(0, "未扫码"),
    SCANNED(1, "已扫码"),
    CONFIRMED(2, "已确认"),
    EXPIRED(3, "已过期");

    // 状态码
    private final int code;
    // 状态描述
    private final String desc;

    QrCodeLoginStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
