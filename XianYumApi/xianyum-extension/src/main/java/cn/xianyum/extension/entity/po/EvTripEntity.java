package cn.xianyum.extension.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行程表(EvTrip)表实体类
 *
 * @author xianyum
 * @date 2026/8/27
 */
@Data
@TableName(value = "ev_trip", autoResultMap = true)
public class EvTripEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 行程开始时间
     */
    private LocalDateTime tripStartTime;

    /**
     * 行程结束时间
     */
    private LocalDateTime tripEndTime;

    /**
     * 行程开始SOC %
     */
    private Integer startSoc;

    /**
     * 起点纬度
     */
    private BigDecimal startLat;

    /**
     * 起点经度
     */
    private BigDecimal startLon;

    /**
     * 起点总里程 km
     */
    private BigDecimal startOdometer;

    /**
     * 起点逆地理解析地址
     */
    private String startAddress;

    /**
     * 行程结束SOC %
     */
    private Integer endSoc;

    /**
     * 终点纬度
     */
    private BigDecimal endLat;

    /**
     * 终点经度
     */
    private BigDecimal endLon;

    /**
     * 终点总里程 km
     */
    private BigDecimal endOdometer;

    /**
     * 终点逆地理解析地址
     */
    private String endAddress;

    /**
     * 行程理论消耗电量 kWh
     */
    private BigDecimal tripConsumeKwh;

    /**
     * 行程平均电池温度℃
     */
    private BigDecimal avgBattTemp;

    /**
     * 行程最低电池温度℃
     */
    private BigDecimal minBattTemp;

    /**
     * 行程最高电池温度℃
     */
    private BigDecimal maxBattTemp;
}
