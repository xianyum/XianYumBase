package cn.xianyum.extension.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * 172号卡商品实体类
 * @author zhangwei
 * @date 2023/10/6 18:52
 */
@Data
public class HaoKaLotProductEntity {

    private Long productID;

    private String productName;

    private String productCode;

    private Double price;

    private String remark;

    private Date createTime;

    /** 佣金 */
    private Double sPrice;

    /** 运营商 */
    private String operator;

    /** 主图url */
    private String mainPic;

    private Long userID;

    /** 归属地 */
    private String area;

    /** 禁发区域 */
    private String disableArea;

    /** 结算规则 */
    private String rule;

    private String age1;

    private String age2;

    private String littlepicture;
}
