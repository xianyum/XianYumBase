package cn.xianyum.proxy.service.impl;

import cn.xianyum.proxy.dao.ProxyPushLogMapper;
import cn.xianyum.proxy.entity.po.ProxyPushLogEntity;
import cn.xianyum.proxy.service.ProxyPushLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2021/7/17 15:37
 */
@Service("proxyPushLogService")
public class ProxyPushLogServiceImpl implements ProxyPushLogService {

    @Autowired
    private ProxyPushLogMapper pushLogMapper;

    @Override
    public int insert(ProxyPushLogEntity pushLogEntity) {
        return pushLogMapper.insert(pushLogEntity);
    }
}
