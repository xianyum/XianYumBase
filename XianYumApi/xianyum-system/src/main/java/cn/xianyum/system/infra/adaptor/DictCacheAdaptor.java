package cn.xianyum.system.infra.adaptor;

import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.system.entity.po.DictDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 字典缓存适配器
 * @author zhangwei
 * @date 2023/9/22 11:45
 */
@Component
public class DictCacheAdaptor {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.dict.type_prefix}")
    private String dictTypeRedisPrefix;


    public boolean hasDictDataByTypeKey(String dictType) {
        return redisUtils.hasKey(dictTypeRedisPrefix+dictType);
    }

    public boolean delDictDataByTypeKey(String dictType) {
        redisUtils.del(dictTypeRedisPrefix+dictType);
        return true;
    }

    public boolean setDictDataByTypeCache(String dictType, List<DictDataEntity> dictDataEntities) {
        Map<String,Object> map = new HashMap<>();
        for(DictDataEntity item : dictDataEntities){
            map.put(item.getId().toString(),item);
        }
        return redisUtils.hMSet(dictTypeRedisPrefix+dictType,map,8L, TimeUnit.HOURS);
    }


    public List<DictDataEntity> getDictDataByTypeCache(String dictType) {
        List<DictDataEntity> dictDataEntities = new ArrayList<>();
        Map<Object, Object> objectObjectMap = redisUtils.hMGet(dictTypeRedisPrefix + dictType);
        for(Map.Entry<Object, Object> entry : objectObjectMap.entrySet()){
            dictDataEntities.add((DictDataEntity)entry.getValue());
        }
        return dictDataEntities;
    }

    /**
     * 删除所有dict缓存
     */
    public void truncateDictDataByTypeKey() {
        String key = dictTypeRedisPrefix+"*";
        Collection<String> keys = redisUtils.keys(key);
        redisUtils.deleteObject(keys);
    }
}
