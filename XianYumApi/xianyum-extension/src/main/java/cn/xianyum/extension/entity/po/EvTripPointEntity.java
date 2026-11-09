package cn.xianyum.extension.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import cn.xianyum.common.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行程轨迹点表(EvTripPoint)表实体类
 * 每次行程拆分后，将有效的遥测数据（经纬度不为空）落库到此表
 *
 * @author xianyum
 * @date 2026/11/9
 */
@Data
@TableName(value = "ev_trip_point", autoResultMap = true)
public class EvTripPointEntity extends BaseEntity {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 关联行程ID
     */
    private Long tripId;

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
     * 当前速度 km/h
     */
    private BigDecimal speed;

    /**
     * 当前动力电池电量百分比 (SOC)
     */
    private Integer soc;

    /**
     * 当前牵引功率 kW（充电时为负值）
     */
    private BigDecimal power;

    /**
     * 电池温度 ℃
     */
    private BigDecimal battTemp;

    /**
     * 车内温度 ℃
     */
    private BigDecimal cabinTemp;

    /**
     * 车外温度 ℃
     */
    private BigDecimal extTemp;

    /**
     * 标称电池容量 kWh
     */
    private BigDecimal capacity;

    /**
     * 总里程 km
     */
    private BigDecimal odometer;

    /**
     * 左前胎压 bar
     */
    private BigDecimal tirePressureFl;

    /**
     * 右前胎压 bar
     */
    private BigDecimal tirePressureFr;

    /**
     * 左后胎压 bar
     */
    private BigDecimal tirePressureRl;

    /**
     * 右后胎压 bar
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
     * 当前会话已充电电量 kWh
     */
    private BigDecimal kwhCharged;
}
