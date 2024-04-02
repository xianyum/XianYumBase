package cn.xianyum.extension.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * 172号卡通知
 * @author zhangwei
 * @date 2023/10/6 18:52
 */
@Data
public class HaoKaLotArticleEntity {

    private Long articleID;

    private String title;

    private String info;

    private Date createTime;
}
