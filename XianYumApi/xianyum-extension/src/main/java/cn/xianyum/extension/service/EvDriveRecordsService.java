package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.common.entity.base.PageResponse;
import com.alibaba.fastjson2.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 新能源车行驶记录(EvDriveRecords)service层
 *
 * @author makejava
 * @since 2025-03-06 20:43:40
 */
public interface EvDriveRecordsService {

    PageResponse<EvDriveRecordsResponse> getPage(EvDriveRecordsRequest request);

    EvDriveRecordsResponse getById(Long id);

    Integer save(EvDriveRecordsRequest request);

    Integer update(EvDriveRecordsRequest request);

    Integer deleteById(Long[] ids);

    /**
     * 校验重复数据
     * @param request
     */
    void checkForDuplicateData(EvDriveRecordsRequest request);

    List<Map<String, Object>> getReportLineData(EvDriveRecordsRequest request);

    /**
     * 判断是否异常数据
     * @param electricityPerKm
     * @return
     */
    boolean checkNormalStatus(BigDecimal electricityPerKm);

    /**
     * 获取某一天的驾驶数据
     * @param driveDate
     * @return
     */
    EvDriveRecordsResponse selectByDay(Date driveDate);
}
