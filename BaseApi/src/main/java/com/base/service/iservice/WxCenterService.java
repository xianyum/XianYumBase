package com.base.service.iservice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.entity.po.wx_center.WxCenterEntity;
import com.base.entity.request.WxCenterRequest;

/**
 * @author zhangwei
 * @date 2019/9/29 13:57
 */
public interface WxCenterService {

    void save(String json);

    IPage<WxCenterEntity> queryAll(WxCenterRequest request);
}
