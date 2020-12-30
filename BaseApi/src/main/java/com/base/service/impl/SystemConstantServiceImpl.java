package com.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.base.common.async.AsyncManager;
import com.base.common.async.factory.AsyncLogFactory;
import com.base.common.async.factory.AsyncSystemConstantFactory;
import com.base.common.utils.RedisUtils;
import com.base.common.utils.StringUtil;
import com.base.dao.SystemConstantMapper;
import com.base.entity.po.SystemConstantEntity;
import com.base.service.iservice.SystemConstantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2020/11/3 19:26
 */
@Service
@Slf4j
public class SystemConstantServiceImpl implements SystemConstantService {

    @Autowired
    private SystemConstantMapper systemConstantMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.system_constant.prefix}")
    private String systemConstantPrefix;

    @Override
    public SystemConstantEntity getPublicConstant(SystemConstantEntity request) {

        SystemConstantEntity systemConstantEntity = getByKey(request.getConstantKey());
        if(systemConstantEntity != null && 0 == systemConstantEntity.getConstantVisible()){
            return systemConstantEntity;
        }
        return null;
    }

    @Override
    public SystemConstantEntity getPrivateConstant(SystemConstantEntity request) {
        return getByKey(request.getConstantKey());
    }

    @Override
    public int update(SystemConstantEntity request) {
        request.setUpdateTime(new Date());
        request.setConstantKey(null);
        int count = systemConstantMapper.updateById(request);
        AsyncManager.async().execute(AsyncSystemConstantFactory.setSystemConstantToRedis(request.getConstantKey(),null));
        return count;
    }

    @Override
    public SystemConstantEntity getByKey(String key) {
        if(StringUtil.isEmpty(key)){
            return null;
        }
        String redisKey = systemConstantPrefix + key;
        SystemConstantEntity byKeyFromRedis = getByKeyFromRedis(redisKey);
        if(byKeyFromRedis != null){
            return byKeyFromRedis;
        }
        QueryWrapper<SystemConstantEntity> queryWrapper
                = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key",key);
        SystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        AsyncManager.async().execute(AsyncSystemConstantFactory.setSystemConstantToRedis(key,systemConstantEntity));
        return systemConstantEntity;
    }

    /**
     * 系统常用参数缓存到redis中
     * @param keyOrId 系统常用参数的id或者key
     * @param systemConstantEntity 如果为null，根据keyOrId查询，如果有值就不用查询了
     * @return
     */
    @Override
    public boolean setSystemConstantToRedis(String keyOrId, SystemConstantEntity systemConstantEntity) {

        if(systemConstantEntity == null){
            QueryWrapper<SystemConstantEntity> queryWrapper
                    = new QueryWrapper<SystemConstantEntity>()
                    .eq("constant_key",keyOrId).or().eq("id","keyOrId");
            systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        }
        if(systemConstantEntity != null){
            String redisKey = systemConstantPrefix+systemConstantEntity.getConstantKey();
            return redisUtils.set(redisKey,systemConstantEntity);
        }
        return false;
    }

    /**
     * 根据key从redis中取常用参数
     * @param key
     * @return
     */
    @Override
    public SystemConstantEntity getByKeyFromRedis(String key) {
        String redisKey = systemConstantPrefix + key;
        SystemConstantEntity systemConstantEntity = (SystemConstantEntity)redisUtils.get(redisKey);
        return systemConstantEntity;
    }

}
