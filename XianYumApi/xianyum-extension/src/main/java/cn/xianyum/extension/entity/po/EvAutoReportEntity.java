package cn.xianyum.extension.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车机系统自动上报-遥测数据(EvAutoReport)表实体类
 *
 * @author xianyum
 * @date 2026/8/25
 */
@Data
@TableName(value = "ev_auto_report", autoResultMap = true)
public class EvAutoReportEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

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
     * 当前速度，km/h
     */
    private BigDecimal speed;

    /**
     * 当前动力电池电量百分比(SOC)
     */
    private Integer soc;

    /**
     * 当前牵引功率kW（充电时为负值）
     */
    private BigDecimal power;

    /**
     * 电池温度°C
     */
    private BigDecimal battTemp;

    /**
     * 车内温度°C
     */
    private BigDecimal cabinTemp;

    /**
     * 车外温度°C
     */
    private BigDecimal extTemp;

    /**
     * 标称电池容量kWh
     */
    private BigDecimal capacity;

    /**
     * 里程km
     */
    private BigDecimal odometer;

    /**
     * 左前胎压bar
     */
    private BigDecimal tirePressureFl;

    /**
     * 右前胎压bar
     */
    private BigDecimal tirePressureFr;

    /**
     * 左后胎压bar
     */
    private BigDecimal tirePressureRl;

    /**
     * 右后胎压bar
     */
    private BigDecimal tirePressureRr;

    /**
     * 是否在充电：0-否，1-是
     */
    private Integer isCharging;

    /**
     * 是否停车：0-否，1-是
     */
    private Integer isParked;

    /**
     * 是否直流快充(DCFC)：0-否，1-是
     */
    private Integer isDcfc;

    /**
     * 当前会话已充电电量kWh
     */
    private BigDecimal kwhCharged;
}
