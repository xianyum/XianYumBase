package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

    List<LogResponse> getVisitCountCharts();

    int getLogCountWithCache(String time);

    void truncateLog();

    Long getLogCount();
}
