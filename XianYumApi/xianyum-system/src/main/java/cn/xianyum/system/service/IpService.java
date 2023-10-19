package cn.xianyum.system.service;


import cn.xianyum.system.entity.po.IpInfoEntity;

/**
 * @author zhangwei
 * @date 2020/4/1 13:46
 */
public interface IpService {

    IpInfoEntity getIpInfo(String ip);

}
