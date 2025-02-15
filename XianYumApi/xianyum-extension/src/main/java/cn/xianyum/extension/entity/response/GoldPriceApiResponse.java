package cn.xianyum.extension.entity.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangwei
 * @date 2025/2/15 22:54
*/
@Data
public class GoldPriceApiResponse {

    @JSONField(name = "resultcode")
    private String resultCode;
    private String reason;
    private List<Result> result;

    @Data
    public static class Result {
        @JSONField(name = "Au99.99")
        private Au99g au99g;
    }

    @Data
    public static class Au99g {

        private String variety;

        /**
         * 最新价
         */
        @JSONField(name = "latestpri")
        private String latestPrice;

        /**
         * 开盘价
         */
        @JSONField(name = "openpri")
        private String openPrice;

        /**
         * 最高价
         */
        @JSONField(name = "maxpri")
        private String maxPrice;

        /**
         * 最低价
         */
        @JSONField(name = "minpri")
        private String minPrice;

        /**
         * 涨跌幅
         */
        @JSONField(name = "limit")
        private String changePercentage;

        /**
         * 昨收价
         */
        @JSONField(name = "yespri")
        private String yesPrice;

        /**
         * 总成交量
         */
        @JSONField(name = "totalvol")
        private String totalVol;

        /**
         * 更新时间
         */
        private String time;

    }
}
