package cn.xianyum.extension.entity.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author xianyum
 * @date 2026/8/28 22:03
 */
@Data
public class EvTripTrackResponse {

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 经度
     */
    private BigDecimal lon;

    /**
     * 航向角
     */
    private BigDecimal heading;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;
}
