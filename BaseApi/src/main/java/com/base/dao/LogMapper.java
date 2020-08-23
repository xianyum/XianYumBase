package com.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.entity.po.LogEntity;
import com.base.entity.request.LogRequest;
import com.base.entity.response.LogResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/6/20 13:51
 */
public interface LogMapper extends BaseMapper<LogEntity> {

    List<LogEntity> queryAll(@Param("logRequest") LogRequest logRequest, Page<LogEntity> page);

    int getCount(LogRequest logRequest);

    List<LogResponse> getVisitCountCharts(LogRequest request);
}
