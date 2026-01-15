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
     * 室内湿度
     */
    private BigDecimal indoorHumidity;

    /**
     * 鱼缸tds值
     */
    private BigDecimal fishTankTds;

    /**
     * 鱼缸温度
     */
    private BigDecimal fishTankTemp;

}
