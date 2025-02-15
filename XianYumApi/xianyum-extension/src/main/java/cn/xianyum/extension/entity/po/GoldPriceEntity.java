package cn.xianyum.extension.entity.po;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 每日黄金金价(GoldPrice)表实体类
 *
 * @author zhangwei
 * @since 2025-02-15 22:40:06
 */
@Data
@TableName("gold_price")
public class GoldPriceEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品种
     */
    private String variety;

    /**
     * 最新价
     */
    private BigDecimal latestPrice;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    private BigDecimal maxPrice;

    /**
     * 最低价
     */
    private BigDecimal minPrice;

    /**
     * 涨跌幅
     */
    private String changePercentage;

    /**
     * 昨收价
     */
    private BigDecimal yesPrice;

    /**
     * 总成交量
     */
    private BigDecimal totalVol;

    /**
     * 更新时间
     */
    private Date time;

}
