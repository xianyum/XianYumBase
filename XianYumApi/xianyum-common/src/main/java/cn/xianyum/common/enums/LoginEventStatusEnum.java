package cn.xianyum.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录事件状态枚举
 */
@Getter
@AllArgsConstructor
public enum LoginEventStatusEnum {

    SUCCESS("SUCCESS", "登录成功"),
    FAIL("FAIL", "登录失败");

    private final String code;
    private final String desc;
}
