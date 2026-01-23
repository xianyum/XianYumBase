package cn.xianyum.extension.entity.po;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.xianyum.framwork.mybatis.handler.StringToListTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 新能源车行驶记录(EvDriveRecords)表实体类
 *
 * @author zhangwei
 * @since 2025-03-06 20:43:40
 */
@Data
@TableName(value = "ev_drive_records",autoResultMap = true)
public class EvDriveRecordsEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
     * 状态
     */
    private Integer status;

    /**
     * 事项
     */
    @TableField(typeHandler = StringToListTypeHandler.class)
    private List<String> matter;
}
