package com.base.service.iservice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.entity.po.LogEntity;
import com.base.entity.request.LogRequest;
import com.base.entity.response.LogResponse;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/6/20 13:52
 */
public interface LogService {
    /**
     * 保存系统日志
     * @param logEntity
     */
    void saveLog(LogEntity logEntity);

    IPage<LogEntity> queryAll(LogRequest request);

    void deleteById(String[] logIdS);

    void push();

    List<LogResponse> getVisitCountCharts(LogRequest request);
}
