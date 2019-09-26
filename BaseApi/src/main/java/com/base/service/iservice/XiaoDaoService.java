package com.base.service.iservice;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.XiaoDaoRequest;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
public interface XiaoDaoService {

    void push();

    IPage<XiaoDaoEntity> queryAll(XiaoDaoRequest request);
}
