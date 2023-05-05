package cn.xianyum.system.service.impl;

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
import java.util.Date;
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
    public int update(SystemConstantRequest request) {
        SecurityUtils.allowAdminAuth();
        SystemConstantEntity bean = BeanUtils.copy(request,SystemConstantEntity.class);
        bean.setUpdateTime(new Date());
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
    public IPage<SystemConstantResponse> getPage(SystemConstantRequest request) {
        SecurityUtils.allowAdminAuth();
        Page<SystemConstantEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<SystemConstantEntity> queryWrapper = new QueryWrapper<SystemConstantEntity>()
                .like(StringUtil.isNotEmpty(request.getConstantKey()),"constant_key",request.getConstantKey())
                .like(StringUtil.isNotEmpty(request.getConstantDescribe()),"constant_describe",request.getConstantDescribe())
                .orderByDesc(Arrays.asList("update_time","create_time"));
        IPage<SystemConstantEntity> pageResult = systemConstantMapper.selectPage(page,queryWrapper);
        IPage<SystemConstantResponse> responseIPage = new Page<>();
        responseIPage.setTotal(pageResult.getTotal());
        responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),SystemConstantResponse.class));
        return responseIPage;
    }

    @Override
    public void deleteByKey(String key) {
        SecurityUtils.allowAdminAuth();
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
    public SystemConstantResponse getById(SystemConstantRequest request) {
        SecurityUtils.allowAdminAuth();
        if(StringUtil.isEmpty(request.getId())){
            throw new SoException("id不能为空");
        }
        SystemConstantEntity result = systemConstantMapper.selectById(request.getId());
        SystemConstantResponse response = BeanUtils.copy(result, SystemConstantResponse.class);
        return response;

    }

    @Override
    public Integer save(SystemConstantRequest request) {
        SecurityUtils.allowAdminAuth();
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
        Date date = new Date();
        bean.setCreateTime(date);
        bean.setUpdateTime(date);
        return systemConstantMapper.insert(bean);
    }

}
