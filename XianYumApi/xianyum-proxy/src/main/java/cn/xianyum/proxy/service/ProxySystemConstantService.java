package cn.xianyum.proxy.service;


import cn.xianyum.proxy.entity.po.ProxySystemConstantEntity;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
public interface ProxySystemConstantService {
    ProxySystemConstantEntity getPublicConstant(ProxySystemConstantEntity request);

    ProxySystemConstantEntity getPrivateConstant(ProxySystemConstantEntity request);

    int update(ProxySystemConstantEntity request);

    ProxySystemConstantEntity getByKey(String key);

    boolean setSystemConstantToRedis(String keyOrId, ProxySystemConstantEntity systemConstantEntity);

    ProxySystemConstantEntity getByKeyFromRedis(String key);
}
