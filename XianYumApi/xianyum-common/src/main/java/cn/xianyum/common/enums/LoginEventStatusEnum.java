package cn.xianyum.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录/登出事件类型枚举
 */
@Getter
@AllArgsConstructor
public enum LoginEventStatusEnum {

    LOGIN("LOGIN", "登录"),
    LOGOUT("LOGOUT", "登出");

    private final String code;
    private final String desc;
}
