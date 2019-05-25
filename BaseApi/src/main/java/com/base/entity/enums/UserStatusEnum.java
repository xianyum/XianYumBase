package com.base.entity.enums;

/***
 * 用户状态枚举类
 * 0：允许
 * 1：禁止
 */
public enum UserStatusEnum {
    ALLOW(0),
    BAN(1);

    UserStatusEnum(Integer status) {
        this.status = status;
    }

    private Integer status;

    public Integer getStatus() {
        return status;
    }
}
