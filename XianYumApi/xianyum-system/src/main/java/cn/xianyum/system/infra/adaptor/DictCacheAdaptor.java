package cn.xianyum.system.infra.adaptor;

import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.system.entity.po.DictDataEntity;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;

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
        return redisUtils.setMin(dictTypeRedisPrefix+dictType, JSONObject.toJSONString(dictDataEntities), 60*80);
    }

    public List<DictDataEntity> getDictDataByTypeCache(String dictType) {
        String redisResult = redisUtils.getString(dictTypeRedisPrefix + dictType);
        return JSONObject.parseObject(redisResult,new TypeReference<List<DictDataEntity>>(){});
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
