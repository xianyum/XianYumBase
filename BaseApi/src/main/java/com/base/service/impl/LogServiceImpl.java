package com.base.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.dao.LogMapper;
import com.base.entity.po.LogEntity;
import com.base.entity.request.LogRequest;
import com.base.service.iservice.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/6/20 13:52
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements LogService {

    @Autowired
    private LogMapper logMapper;


    /**
     * 保存系统日志
     *
     * @param logEntity
     */
    @Override
    public void saveLog(LogEntity logEntity) {
        logMapper.insert(logEntity);
    }

    @Override
    public IPage<LogEntity> queryAll(LogRequest request) {
        Page<LogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        //查询总记录数
        page.setSearchCount(true);
        List<LogEntity> list = logMapper.queryAll(request, page);
        page.setRecords(list);
        return page;
    }
}
