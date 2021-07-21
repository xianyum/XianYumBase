package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
