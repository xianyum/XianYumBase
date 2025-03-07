package cn.xianyum.extension.entity.request;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 新能源车行驶记录(EvDriveRecords)request请求实体
 *
 * @author makejava
 * @since 2025-03-06 20:43:41
 */
@Data
public class EvDriveRecordsExcel{

    /**
     * 驾驶日期
     */
    @ExcelProperty("驾驶日期")
    private Date driveDate;

    /**
     * 行驶公里数(单位km)
     */
    @ExcelProperty("行驶公里数")
    private Integer distanceKm;

    /**
     * 消耗的电量(单位kWh)
     */
    @ExcelProperty("消耗电量")
    private BigDecimal electricityConsumed;


}
