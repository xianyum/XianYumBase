package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.dao.SystemConstantMapper;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.request.SystemConstantRequest;
import cn.xianyum.system.entity.response.SystemConstantResponse;
import cn.xianyum.system.service.SystemConstantService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

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
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.system_constant.prefix}")
    private String systemConstantPrefix;

    @Override
    public SystemConstantEntity getPublicConstant(String key) {

        SystemConstantEntity systemConstantEntity = getByKey(key);
        if(systemConstantEntity != null && 0 == systemConstantEntity.getConstantVisible()){
            return systemConstantEntity;
        }
        return null;
    }

    @Override
    public SystemConstantEntity getPrivateConstant(String key) {
        return getByKey(key);
    }

    @Override
    public int update(SystemConstantRequest request) {
        SystemConstantEntity bean = BeanUtils.copy(request,SystemConstantEntity.class);
        bean.setConstantKey(null);
        int count = systemConstantMapper.updateById(bean);
        xianYumTaskExecutor.execute(()-> SpringUtils.getBean(SystemConstantService.class).setSystemConstantToRedis(request.getConstantKey(),null));
        return count;
    }

    @Override
    public SystemConstantEntity getByKey(String key) {
        if(StringUtil.isEmpty(key)){
            return null;
        }
        SystemConstantEntity byKeyFromRedis = getByKeyFromRedis(key);
        if(byKeyFromRedis != null){
            return byKeyFromRedis;
        }
        QueryWrapper<SystemConstantEntity> queryWrapper
                = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key",key);
        SystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        xianYumTaskExecutor.execute(()-> SpringUtils.getBean(SystemConstantService.class).setSystemConstantToRedis(key,systemConstantEntity));
        return systemConstantEntity;
    }

    @Override
    public String getValueKey(String key) {
        SystemConstantEntity systemConstant = this.getByKey(key);
        return Objects.nonNull(systemConstant)?systemConstant.getConstantValue():null;
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
                    .eq("constant_key",keyOrId).or().eq("id",keyOrId);
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


    @Override
    public PageResponse<SystemConstantResponse> getPage(SystemConstantRequest request) {
        Page<SystemConstantEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<SystemConstantEntity> queryWrapper = new QueryWrapper<SystemConstantEntity>()
                .like(StringUtil.isNotEmpty(request.getConstantKey()),"constant_key",request.getConstantKey())
                .like(StringUtil.isNotEmpty(request.getConstantDescribe()),"constant_describe",request.getConstantDescribe())
                .orderByDesc(Arrays.asList("update_time","create_time"));
        IPage<SystemConstantEntity> pageResult = systemConstantMapper.selectPage(page,queryWrapper);
        return PageResponse.of(pageResult,SystemConstantResponse.class);
    }

    @Override
    public void deleteByKey(String key) {
        if(StringUtil.isEmpty(key)){
            throw new SoException("系统常量键不能为空");
        }
        QueryWrapper<SystemConstantEntity> queryWrapper = new QueryWrapper<SystemConstantEntity>().eq("constant_key",key);
        systemConstantMapper.delete(queryWrapper);
        String redisKey = systemConstantPrefix + key;
        redisUtils.del(redisKey);
    }

    @Override
    public void deleteRedisCache(String key) {
        if(StringUtil.isEmpty(key)){
            throw new SoException("key不能为空");
        }
        String redisKey = systemConstantPrefix + key;
        redisUtils.del(redisKey);
    }

    @Override
    public void refreshCache() {
        Collection<String> keys = redisUtils.keys(systemConstantPrefix + "*");
        redisUtils.deleteObject(keys);

    }

    /**
     * 根据key新增或者更新
     *
     * @param systemConstantKeyEnum
     * @param value
     * @param visible
     */
    @Override
    public boolean saveOrUpdate(SystemConstantKeyEnum systemConstantKeyEnum,String value, Integer visible) {
        String key = systemConstantKeyEnum.getKey();
        QueryWrapper<SystemConstantEntity> queryWrapper = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key",key);
        SystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper);
        SystemConstantRequest systemConstantRequest = new SystemConstantRequest();
        systemConstantRequest.setConstantValue(value);
        systemConstantRequest.setConstantDescribe(systemConstantKeyEnum.getDesc());
        systemConstantRequest.setConstantVisible(visible);
        systemConstantRequest.setConstantKey(key);
        if(Objects.nonNull(systemConstantEntity)){
            systemConstantRequest.setId(systemConstantEntity.getId());
            return this.update(systemConstantRequest) > 0 ;
        }else{
            return this.save(systemConstantRequest) > 0;
        }
    }

    @Override
    public SystemConstantResponse getById(String id) {
        SystemConstantEntity result = systemConstantMapper.selectById(id);
        SystemConstantResponse response = BeanUtils.copy(result, SystemConstantResponse.class);
        return response;

    }

    @Override
    public Integer save(SystemConstantRequest request) {
        if(StringUtil.isBlank(request.getConstantValue())){
            throw new SoException("系统常量值不能为空");
        }
        QueryWrapper<SystemConstantEntity> queryRepeatWrapper = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key",request.getConstantKey());
        SystemConstantEntity repeatSystemConstant = systemConstantMapper.selectOne(queryRepeatWrapper);
        if (Objects.nonNull(repeatSystemConstant)) {
            throw new SoException("系统常量键已存在");
        }
        SystemConstantEntity bean = BeanUtils.copy(request,SystemConstantEntity.class);
        bean.setId(UUIDUtils.UUIDReplace());
        return systemConstantMapper.insert(bean);
    }



}
