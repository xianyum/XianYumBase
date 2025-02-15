package cn.xianyum.extension.entity.response;

import java.math.BigDecimal;
import java.util.Date;
import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 每日黄金金价(GoldPrice)response返回实体
 *
 * @author zhangwei
 * @since 2025-02-15 22:40:06
 */
@Data
public class GoldPriceResponse extends BaseResponse {
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
