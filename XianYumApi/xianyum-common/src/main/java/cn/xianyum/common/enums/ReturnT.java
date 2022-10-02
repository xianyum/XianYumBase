package cn.xianyum.common.enums;

/**
 * @author zhangwei
 * @date 2022/5/12 21:28
 */
public enum ReturnT {

    SUCCESS("success"),
    FAILURE("failure");

    private final String value;

    private ReturnT(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
