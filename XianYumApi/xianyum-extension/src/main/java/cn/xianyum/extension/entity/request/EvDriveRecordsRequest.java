package cn.xianyum.extension.entity.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 新能源车行驶记录(EvDriveRecords)request请求实体
 *
 * @author makejava
 * @since 2025-03-06 20:43:41
 */
@Data
public class EvDriveRecordsRequest extends BaseRequest {
    private Long id;

    /**
     * 车牌号
     */
    private String vehicleNo;

    /**
     * 驾驶日期
     */
    private Date driveDate;

    /**
     * 行驶公里数(单位km)
     */
    private Integer distanceKm;

    /**
     * 消耗的电量(单位kWh)
     */
    private BigDecimal electricityConsumed;

    /**
     * 每公里的电量消耗(kWh/km)
     */
    private BigDecimal electricityPerKm;

    /**
     * 时间类型
     */
    private Integer dateType;

    /**
     * 时间范围
     */
    private List<String> timeRange;

    /**
     * 状态 0 正常 1 异常
     */
    private Integer status;

    /**
     * 事项
     */
    private String matter;
}
