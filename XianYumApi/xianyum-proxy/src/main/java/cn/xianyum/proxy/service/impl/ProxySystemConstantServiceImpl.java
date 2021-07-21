package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.proxy.common.factory.ProxyAsyncSystemConstantFactory;
import cn.xianyum.proxy.dao.ProxySystemConstantMapper;
import cn.xianyum.proxy.entity.po.ProxySystemConstantEntity;
import cn.xianyum.proxy.service.ProxySystemConstantService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public class ProxySystemConstantServiceImpl implements ProxySystemConstantService {

    @Autowired
    private ProxySystemConstantMapper systemConstantMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.system_constant.prefix}")
    private String systemConstantPrefix;

    @Override
    public ProxySystemConstantEntity getPublicConstant(ProxySystemConstantEntity request) {

        ProxySystemConstantEntity systemConstantEntity = getByKey(request.getConstantKey());
        if(systemConstantEntity != null && 0 == systemConstantEntity.getConstantVisible()){
            return systemConstantEntity;
        }
        return null;
    }

    @Override
    public ProxySystemConstantEntity getPrivateConstant(ProxySystemConstantEntity request) {
        return getByKey(request.getConstantKey());
    }

    @Override
    public int update(ProxySystemConstantEntity request) {
        request.setUpdateTime(new Date());
        request.setConstantKey(null);
        int count = systemConstantMapper.updateById(request);
        AsyncManager.async().execute(ProxyAsyncSystemConstantFactory.setSystemConstantToRedis(request.getConstantKey(),null));
        return count;
    }

    @Override
    public ProxySystemConstantEntity getByKey(String key) {
        if(StringUtil.isEmpty(key)){
            return null;
        }
        String redisKey = systemConstantPrefix + key;
        ProxySystemConstantEntity byKeyFromRedis = getByKeyFromRedis(redisKey);
        if(byKeyFromRedis != null){
            return byKeyFromRedis;
        }
        QueryWrapper<ProxySystemConstantEntity> queryWrapper
                = new QueryWrapper<ProxySystemConstantEntity>()
                .eq("constant_key",key);
        ProxySystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        AsyncManager.async().execute(ProxyAsyncSystemConstantFactory.setSystemConstantToRedis(key,systemConstantEntity));
        return systemConstantEntity;
    }

    /**
     * 系统常用参数缓存到redis中
     * @param keyOrId 系统常用参数的id或者key
     * @param systemConstantEntity 如果为null，根据keyOrId查询，如果有值就不用查询了
     * @return
     */
    @Override
    public boolean setSystemConstantToRedis(String keyOrId, ProxySystemConstantEntity systemConstantEntity) {

        if(systemConstantEntity == null){
            QueryWrapper<ProxySystemConstantEntity> queryWrapper
                    = new QueryWrapper<ProxySystemConstantEntity>()
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
    public ProxySystemConstantEntity getByKeyFromRedis(String key) {
        String redisKey = systemConstantPrefix + key;
        ProxySystemConstantEntity systemConstantEntity = (ProxySystemConstantEntity)redisUtils.get(redisKey);
        return systemConstantEntity;
    }

}
