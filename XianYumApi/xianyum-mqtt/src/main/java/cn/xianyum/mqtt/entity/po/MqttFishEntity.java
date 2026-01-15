package cn.xianyum.mqtt.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * (MqttFish)表实体类
 *
 * @author makejava
 * @since 2026-01-15 22:04:02
 */
@Data
@TableName("mqtt_fish")
public class MqttFishEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
