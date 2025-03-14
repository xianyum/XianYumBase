package cn.xianyum.system.service;


import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.request.SystemConstantRequest;
import cn.xianyum.system.entity.response.SystemConstantResponse;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
public interface SystemConstantService {
    SystemConstantEntity getPublicConstant(String key);

    SystemConstantEntity getPrivateConstant(String key);

    int update(SystemConstantRequest request);

    SystemConstantEntity getByKey(String key);

    String getValueKey(String key);

    boolean setSystemConstantToRedis(String keyOrId,SystemConstantEntity systemConstantEntity);

    SystemConstantEntity getByKeyFromRedis(String key);


    Integer save(SystemConstantRequest request);

    SystemConstantResponse getById(String id);

    PageResponse<SystemConstantResponse> getPage(SystemConstantRequest request);

    void deleteByKey(String key);

    void deleteRedisCache(String key);

    void refreshCache();

    /**
     * 根据key新增或者更新
     * @param systemConstantKeyEnum
     * @param value
     * @param visible
     */
    boolean saveOrUpdate(SystemConstantKeyEnum systemConstantKeyEnum,String value, Integer visible);
}
