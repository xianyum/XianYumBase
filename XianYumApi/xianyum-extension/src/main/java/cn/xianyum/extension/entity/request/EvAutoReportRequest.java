package cn.xianyum.extension.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 车机系统自动上报 - 遥测数据请求实体
 * 字段与单位与 Iternio Telemetry API 保持一致：
 *   utc 为秒, power 为 kW, 温度为 °C, 胎压为 bar, odometer 为 km
 * 没有数据的字段直接不出现（可选）
 * @author xianyum
 * @date 2026/8/25 20:45
 */
@Data
public class EvAutoReportRequest extends BaseRequest {

    /**
     * UTC 时间戳（秒）
     */
    private Long utc;

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
     * 当前速度，km/h（停车时可能缺失）
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
     * 电池温度 °C
     */
    @JsonProperty("batt_temp")
    private BigDecimal battTemp;

    /**
     * 车内温度 °C
     */
    @JsonProperty("cabin_temp")
    private BigDecimal cabinTemp;

    /**
     * 车外温度 °C
     */
    @JsonProperty("ext_temp")
    private BigDecimal extTemp;

    /**
     * 标称电池容量 kWh
     */
    private BigDecimal capacity;

    /**
     * 里程 km
     */
    private BigDecimal odometer;

    /**
     * 左前胎压 bar
     */
    @JsonProperty("tire_pressure_fl")
    private BigDecimal tirePressureFl;

    /**
     * 右前胎压 bar
     */
    @JsonProperty("tire_pressure_fr")
    private BigDecimal tirePressureFr;

    /**
     * 左后胎压 bar
     */
    @JsonProperty("tire_pressure_rl")
    private BigDecimal tirePressureRl;

    /**
     * 右后胎压 bar
     */
    @JsonProperty("tire_pressure_rr")
    private BigDecimal tirePressureRr;

    /**
     * 是否在充电：0-否，1-是
     */
    @JsonProperty("is_charging")
    private Integer isCharging;

    /**
     * 是否停车：0-否，1-是
     */
    @JsonProperty("is_parked")
    private Integer isParked;

    /**
     * 是否直流快充（DCFC）：0-否，1-是（充电时才有）
     */
    @JsonProperty("is_dcfc")
    private Integer isDcfc;

    /**
     * 当前会话已充电电量 kWh（充电时才有）
     */
    @JsonProperty("kwh_charged")
    private BigDecimal kwhCharged;
}
