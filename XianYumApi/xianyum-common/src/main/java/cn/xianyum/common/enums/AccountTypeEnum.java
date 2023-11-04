package cn.xianyum.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
 * @author zhangwei
 * @date 2021/1/28 23:25
 */
public enum AccountTypeEnum {

    SYSTEM("0","系统用户"),
    ZHI_FU_BAO("1","支付宝"),
    QQ("2","QQ");

    private String accountType;

    private String desc;


    AccountTypeEnum(String accountType, String desc) {
        this.accountType = accountType;
        this.desc = desc;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getDesc() {
        return desc;
    }

    public static AccountTypeEnum getAccountType(String accountType) {
        Optional<AccountTypeEnum> first = Arrays.stream(AccountTypeEnum.values())
                .filter(e -> e.getAccountType().equals(accountType))
                .findFirst();
        return first.isPresent()?first.get():null;
    }
}
