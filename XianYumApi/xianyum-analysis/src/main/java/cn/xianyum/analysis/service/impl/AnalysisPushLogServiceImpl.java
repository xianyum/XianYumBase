package cn.xianyum.analysis.service.impl;

import cn.xianyum.analysis.dao.AnalysisPushLogMapper;
import cn.xianyum.analysis.entity.po.AnalysisPushLogEntity;
import cn.xianyum.analysis.service.AnalysisPushLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2021/7/17 15:37
 */
@Service("analysisPushLogService")
public class AnalysisPushLogServiceImpl implements AnalysisPushLogService {

    @Autowired
    private AnalysisPushLogMapper pushLogMapper;

    @Override
    public int insert(AnalysisPushLogEntity pushLogEntity) {
        return pushLogMapper.insert(pushLogEntity);
    }
}
