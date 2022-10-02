package cn.xianyum.proxy.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.service.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.proxy.dao.ProxyDetailsMapper;
import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.metrics.MetricsCollector;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

/**
 * @author wei.zhang
 * @description 定时刷入远程当前写入量和读取量
 * @date 2022/2/15 16:34
 */
@Slf4j
@JobHandler("proxyFlushWriteAndReadBytes")
public class ProxyDetailsFlushWriteAndReadBytes implements IJobHandler {

    @Autowired
    private ProxyDetailsMapper proxyDetailsMapper;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        List<ProxyDetailsEntity> proxyDetailsEntities = proxyDetailsMapper.selectList(new QueryWrapper<>());
        for(ProxyDetailsEntity item : proxyDetailsEntities){
            MetricsCollector collector = MetricsCollector.getCollector(item.getInetPort());
            long nowWroteBytes = collector.getWroteBytes().get();
            long nowReadBytes = collector.getReadBytes().get();
            proxyDetailsMapper.flushBytes(item.getId(),nowWroteBytes,nowReadBytes);
        }
        return ReturnT.SUCCESS;
    }
}
