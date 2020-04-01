package com.base.service.iservice;

import com.base.entity.po.IpInfoEntity;

/**
 * @author zhangwei
 * @date 2020/4/1 13:46
 */
public interface IpService {

    IpInfoEntity getIpInfo(String ip);

}
