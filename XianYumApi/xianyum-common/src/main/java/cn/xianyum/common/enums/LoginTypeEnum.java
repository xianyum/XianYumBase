package cn.xianyum.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
 * @author zhangwei
 * @date 2021/1/28 23:25
 */
public enum LoginTypeEnum {

    SYSTEM("0","系统用户"),
    ZHI_FU_BAO("1","支付宝"),
    QQ("2","QQ"),
    XIAN_YU("3","咸鱼客户端");

    private String loginType;

    private String desc;


    LoginTypeEnum(String loginType, String desc) {
        this.loginType = loginType;
        this.desc = desc;
    }

    public String getAccountType() {
        return loginType;
    }

    public String getDesc() {
        return desc;
    }

    public static LoginTypeEnum getLoginTypeEnum(String loginType) {
        Optional<LoginTypeEnum> first = Arrays.stream(LoginTypeEnum.values())
                .filter(e -> e.getAccountType().equals(loginType))
                .findFirst();
        return first.isPresent()?first.get():null;
    }
}
