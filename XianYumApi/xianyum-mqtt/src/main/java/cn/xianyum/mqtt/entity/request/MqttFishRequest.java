package cn.xianyum.mqtt.entity.request;

import java.math.BigDecimal;
import java.util.Date;
import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * (MqttFish)request请求实体
 *
 * @author zhangwei
 * @since 2026-01-15 22:04:03
 */
@Data
public class MqttFishRequest extends BaseRequest {

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

    /**
     * 时间类型
     */
    private Integer dateType;
}
