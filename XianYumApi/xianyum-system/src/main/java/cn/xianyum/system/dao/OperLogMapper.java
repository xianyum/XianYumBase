package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.entity.request.OperLogRequest;
import cn.xianyum.system.entity.response.OperLogResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 操作日志 Mapper
 */
public interface OperLogMapper extends BaseMapper<OperLogEntity> {

    Long getCount(OperLogRequest operLogRequest);

    List<OperLogResponse> getVisitCountCharts(OperLogRequest request);


    /**
     * 清空操作日志
     */
    void truncateLog();
}
