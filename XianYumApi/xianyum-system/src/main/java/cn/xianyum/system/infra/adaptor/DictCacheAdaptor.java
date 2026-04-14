package cn.xianyum.system.infra.adaptor;

import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.system.entity.po.DictDataEntity;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
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


    public boolean hasDictDataByTypeKey(String dictType) {
        return redisUtils.hasKey(RedisKeyEnum.DICT_TYPE_PREFIX.getKey() +dictType);
    }

    public boolean delDictDataByTypeKey(String dictType) {
        redisUtils.del(RedisKeyEnum.DICT_TYPE_PREFIX.getKey() +dictType);
        return true;
    }

    public boolean setDictDataByTypeCache(String dictType, List<DictDataEntity> dictDataEntities) {
        return redisUtils.setMin(RedisKeyEnum.DICT_TYPE_PREFIX.getKey() +dictType, JSONObject.toJSONString(dictDataEntities), 60*80);
    }

    public List<DictDataEntity> getDictDataByTypeCache(String dictType) {
        String redisResult = redisUtils.getString(RedisKeyEnum.DICT_TYPE_PREFIX.getKey()  + dictType);
        return JSONObject.parseObject(redisResult,new TypeReference<List<DictDataEntity>>(){});
    }

    /**
     * 删除所有dict缓存
     */
    public void truncateDictDataByTypeKey() {
        String key = RedisKeyEnum.DICT_TYPE_PREFIX.getKey() +"*";
        Collection<String> keys = redisUtils.keys(key);
        redisUtils.deleteObject(keys);
    }
}
