package cn.xianyum.common.enums;

/***
 * 用户状态枚举类
 * 0：允许
 * 1：禁止
 */
public enum YesOrNoEnum {
    // 代表正常，允许，未被删除等
    YES(0),
    NO(1);

    YesOrNoEnum(Integer status) {
        this.status = status;
    }

    private Integer status;

    public Integer getStatus() {
        return status;
    }
}
