package cn.xianyum.common.service;

import cn.xianyum.common.entity.IpInfoEntity;

/**
 * @author zhangwei
 * @date 2020/4/1 13:46
 */
public interface IpService {

    IpInfoEntity getIpInfo(String ip);

}
