package cn.xianyum.system.common.enums;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2024/3/5 14:25
 */
public enum ThirdTypeEnum {

    QQ(0,"QQ"),
    ZHI_FU_BAO(1,"支付宝");

    private Integer thirdType;
    private String desc;

    ThirdTypeEnum(Integer thirdType, String desc) {
        this.thirdType = thirdType;
        this.desc = desc;
    }

    public Integer getThirdType() {
        return thirdType;
    }

    public String getDesc() {
        return desc;
    }
}
