package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.entity.request.OperLogRequest;
import cn.xianyum.system.entity.response.OperLogResponse;

import java.util.List;

/**
 * 操作日志 Service
 */
public interface OperLogService {

    void saveOperLog(OperLogEntity operLogEntity);

    PageResponse<OperLogResponse> getPage(OperLogRequest request);

    List<OperLogResponse> getVisitCountCharts();

    void deleteByIds(Long[] ids);

    void truncateAll();

    Long getOperLogCount();
}
