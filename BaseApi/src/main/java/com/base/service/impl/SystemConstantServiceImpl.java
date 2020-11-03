package com.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.base.dao.SystemConstantMapper;
import com.base.entity.po.SystemConstantEntity;
import com.base.service.iservice.SystemConstantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
@Service
@Slf4j
public class SystemConstantServiceImpl implements SystemConstantService {

    @Autowired
    private SystemConstantMapper systemConstantMapper;


    @Override
    public SystemConstantEntity getPublicConstant(SystemConstantEntity request) {

        QueryWrapper<SystemConstantEntity> queryWrapper
                = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key",request.getConstantKey()).eq("constant_visible",0);
        SystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        return systemConstantEntity;
    }

}
