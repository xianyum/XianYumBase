package cn.xianyum.common.enums;

import java.util.Arrays;
import java.util.Optional;

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

    public static LoginTypeEnum getLoginType(Integer loginType) {
        Optional<LoginTypeEnum> first = Arrays.stream(LoginTypeEnum.values())
                .filter(e -> e.getLoginType().equals(loginType))
                .findFirst();
        return first.isPresent()?first.get():null;
    }
}
