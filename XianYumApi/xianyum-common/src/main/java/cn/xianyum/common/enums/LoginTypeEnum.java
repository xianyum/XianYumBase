package cn.xianyum.common.enums;

/**
 * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
 * @author zhangwei
 * @date 2021/1/28 23:25
 */
public enum LoginTypeEnum {

    ACCOUNT(0),
    ZHI_FU_BAO(2),
    QQ(1);
    private Integer loginType;

    LoginTypeEnum(Integer loginType) {
        this.loginType = loginType;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }
}
