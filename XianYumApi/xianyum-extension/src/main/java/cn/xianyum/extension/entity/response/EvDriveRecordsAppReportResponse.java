package cn.xianyum.extension.entity.response;

import lombok.Data;

/**
 * 新能源app报表相应
 * @author xianyum
 * @date 2026/1/13 21:32
 */
@Data
public class EvDriveRecordsAppReportResponse {

    /** 上月数据 */
    private EvDriveRecordsSummaryResponse lastMonthResponse;

    /** 当月数据 */
    private EvDriveRecordsSummaryResponse currentMonthResponse;

    /** 近一年数据 */
    private EvDriveRecordsSummaryResponse lastYearResponse;
}
