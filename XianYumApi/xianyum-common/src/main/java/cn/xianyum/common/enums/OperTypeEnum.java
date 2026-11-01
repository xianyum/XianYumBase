package cn.xianyum.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 操作类型枚举
 */
public enum OperTypeEnum {

    ADD("ADD", "新增"),
    EDIT("EDIT", "修改"),
    DELETE("DELETE", "删除"),
    QUERY("QUERY", "查询"),
    EXPORT("EXPORT", "导出"),
    IMPORT("IMPORT", "导入"),
    LOGIN("LOGIN", "登录"),
    LOGOUT("LOGOUT", "登出"),
    OTHER("OTHER", "其他");

    private final String code;

    private final String desc;

    OperTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OperTypeEnum getByCode(String code) {
        Optional<OperTypeEnum> first = Arrays.stream(OperTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
        return first.orElse(null);
    }
}
