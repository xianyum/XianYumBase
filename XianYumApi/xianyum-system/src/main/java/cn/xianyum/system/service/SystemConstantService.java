package cn.xianyum.system.service;


import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.request.SystemConstantRequest;
import cn.xianyum.system.entity.response.SystemConstantResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
public interface SystemConstantService {
    SystemConstantEntity getPublicConstant(SystemConstantEntity request);

    SystemConstantEntity getPrivateConstant(SystemConstantEntity request);

    int update(SystemConstantRequest request);

    SystemConstantEntity getByKey(String key);

    String getValueKey(String key);

    boolean setSystemConstantToRedis(String keyOrId,SystemConstantEntity systemConstantEntity);

    SystemConstantEntity getByKeyFromRedis(String key);


    Integer save(SystemConstantRequest request);

    SystemConstantResponse getById(SystemConstantRequest request);

    IPage<SystemConstantResponse> getPage(SystemConstantRequest request);

    void deleteByKey(String key);

    void deleteRedisCache(String key);

    void refreshCache();
}
