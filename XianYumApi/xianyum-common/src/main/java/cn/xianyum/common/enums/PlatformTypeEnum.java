package cn.xianyum.common.enums;

/**
 * 平台类型枚举（区分PC/Uniapp）
 * @author xianyum
 * @date 2026/3/10 21:38
 */
public enum PlatformTypeEnum {

    /**
     * PC端
     */
    PC("0", "pc"),

    /**
     * app端
     */
    APP("1", "app");

    /**
     * 枚举值（存储到数据库/Redis的标识，0-pc 1-uniapp）
     */
    private final String code;

    /**
     * 枚举英文标识（便于前端/接口使用）
     */
    private final String value;

    PlatformTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
