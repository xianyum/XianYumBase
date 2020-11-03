package com.base.service.iservice;

import com.base.entity.po.SystemConstantEntity;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
public interface SystemConstantService {
    SystemConstantEntity getPublicConstant(SystemConstantEntity request);
}
