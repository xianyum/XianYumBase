package cn.xianyum.mqtt.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;
import java.math.BigDecimal;

/**
 * (MqttFish)response返回实体
 *
 * @author makejava
 * @since 2026-01-15 22:04:04
 */
@Data
public class MqttFishResponse extends BaseResponse {

    private Long id;

    /**
     * 室内温度
     */
    private BigDecimal indoorTemp;

    /**
     * 室内温度趋势
     */
    private String indoorTempTrend;

    /**
     * 室内湿度
     */
    private BigDecimal indoorHumidity;

    /**
     * 室内湿度趋势
     */
    private String indoorHumidityTrend;

    /**
     * 鱼缸tds值
     */
    private BigDecimal fishTankTds;

    /**
     * 鱼缸tds值趋势
     */
    private String fishTankTdsTrend;

    /**
     * 鱼缸温度
     */
    private BigDecimal fishTankTemp;

    /**
     * 鱼缸温度趋势
     */
    private String fishTankTempTrend;

}
