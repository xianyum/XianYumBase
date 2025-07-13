package cn.xianyum.extension.entity.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangwei
 * @date 2025/7/13 21:56
 */
@Data
public class EvDriveRecordsSummaryResponse {

    // 总行驶里程（公里）
    private BigDecimal totalDistanceKm;

    // 总耗电量（单位根据业务而定，如度）
    private BigDecimal totalElectricityConsumed;

    // 每公里耗电量（保留两位小数）
    private BigDecimal electricityPerKm;
}
