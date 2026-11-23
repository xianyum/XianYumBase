package cn.xianyum.system.service.impl;

import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.ApiKeyProvider;
import cn.xianyum.common.utils.IdGeneratorUtil;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.system.dao.ApiKeyMapper;
import cn.xianyum.system.entity.po.ApiKeyEntity;
import cn.xianyum.system.entity.request.ApiKeyRequest;
import cn.xianyum.system.entity.response.ApiKeyResponse;
import cn.xianyum.system.service.ApiKeyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * API Key 服务实现
 */
@Service
public class ApiKeyServiceImpl implements ApiKeyService,ApiKeyProvider {

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<String> getScopeList() {
        RequestMappingHandlerMapping handlerMapping = SpringUtil.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        return handlerMapping.getHandlerMethods().values().stream()
                .map(HandlerMethod::getMethod)
                .map(this::getSaCheckApiKey)
                .filter(Objects::nonNull)
                .flatMap(annotation -> Arrays.stream(annotation.scope()))
                .filter(StrUtil::isNotBlank)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取方法或其所在类上的 @SaCheckApiKey 注解
     */
    private SaCheckApiKey getSaCheckApiKey(Method method) {
        SaCheckApiKey annotation = AnnotationUtils.findAnnotation(method, SaCheckApiKey.class);
        if (Objects.isNull(annotation)) {
            annotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), SaCheckApiKey.class);
        }
        return annotation;
    }

    @Override
    public PageResponse<ApiKeyResponse> getPage(ApiKeyRequest request) {
        Page<ApiKeyEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<ApiKeyEntity> queryWrapper = new LambdaQueryWrapper<ApiKeyEntity>()
                .like(StrUtil.isNotBlank(request.getTitle()), ApiKeyEntity::getTitle, request.getTitle())
                .eq(StrUtil.isNotBlank(request.getLoginId()), ApiKeyEntity::getLoginId, request.getLoginId())
                .eq(Objects.nonNull(request.getIsValid()), ApiKeyEntity::getIsValid, request.getIsValid())
                .eq(StrUtil.isNotBlank(request.getApiKey()), ApiKeyEntity::getApiKey, request.getApiKey())
                .orderByDesc(ApiKeyEntity::getId);
        IPage<ApiKeyEntity> pageResult = apiKeyMapper.selectPage(page, queryWrapper);
        PageResponse<ApiKeyResponse> response = PageResponse.of(pageResult, ApiKeyResponse.class);
        fillRequestCount(response.getDataList());
        return response;
    }

    @Override
    public ApiKeyResponse getById(Long id) {
        ApiKeyEntity entity = apiKeyMapper.selectById(id);
        if (Objects.isNull(entity)) {
            throw new SoException("ApiKey不存在");
        }
        ApiKeyResponse response = BeanUtil.copyProperties(entity, ApiKeyResponse.class);
        fillRequestCount(List.of(response));
        return response;
    }

    /**
     * 批量回填请求次数：今日次数 + 累计次数
     */
    private void fillRequestCount(List<ApiKeyResponse> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        String statKey = RedisKeyEnum.API_KEY_DAY_STAT.formatKey(DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT));
        Map<Object, Object> statMap = redisUtils.hMGet(statKey);
        for (ApiKeyResponse item : dataList) {
            long todayCount = Convert.toLong(statMap.get(item.getApiKey()), 0L);
            item.setTodayRequestCount(todayCount);
            item.setTotalRequestCount(Convert.toLong(item.getTotalRequestCount(), 0L) + todayCount);
        }
    }

    @Override
    public ApiKeyResponse getByApiKey(String apiKey) {
        if (StrUtil.isBlank(apiKey)) {
            throw new SoException("ApiKey不能为空");
        }
        ApiKeyEntity entity = apiKeyMapper.selectOne(new LambdaQueryWrapper<ApiKeyEntity>().eq(ApiKeyEntity::getApiKey, apiKey.trim()).last("limit 1"));
        if (Objects.isNull(entity)) {
            throw new SoException("ApiKey不存在");
        }
        return BeanUtil.copyProperties(entity, ApiKeyResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(ApiKeyRequest request) {
        if (StrUtil.isBlank(request.getLoginId())) {
            throw new SoException("归属账号ID不能为空");
        }
        String apiKey = StrUtil.isBlank(request.getApiKey())
                ? SaApiKeyUtil.createApiKeyModel(request.getLoginId()).getApiKey()
                : request.getApiKey().trim();
        Long count = apiKeyMapper.selectCount(new LambdaQueryWrapper<ApiKeyEntity>().eq(ApiKeyEntity::getApiKey, apiKey));
        if (count > 0) {
            throw new SoException("ApiKey已存在");
        }
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(IdGeneratorUtil.nextId());
        entity.setApiKey(apiKey);
        entity.setTitle(StrUtil.blankToDefault(request.getTitle(), ""));
        entity.setIntro(StrUtil.blankToDefault(request.getIntro(), ""));
        entity.setLoginId(request.getLoginId());
        entity.setScopes(request.getScopes());
        entity.setExpiresTime(Objects.isNull(request.getExpiresTime()) ? -1L : request.getExpiresTime());
        entity.setIsValid(Objects.isNull(request.getIsValid()) ? Boolean.TRUE : request.getIsValid());
        return apiKeyMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(ApiKeyRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("主键ID不能为空");
        }
        ApiKeyEntity apiKeyEntity = apiKeyMapper.selectById(request.getId());
        if (Objects.isNull(apiKeyEntity)) {
            throw new SoException("ApiKey不存在");
        }
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(request.getId());
        entity.setTitle(request.getTitle());
        entity.setIntro(request.getIntro());
        entity.setScopes(request.getScopes());
        entity.setExpiresTime(request.getExpiresTime());
        entity.setIsValid(request.getIsValid());
        SaApiKeyUtil.deleteApiKey(apiKeyEntity.getApiKey());
        return apiKeyMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new SoException("请选择要删除的数据");
        }
        List<Long> idList = Arrays.asList(ids);
        apiKeyMapper.selectByIds(idList).forEach(apiKey -> SaApiKeyUtil.deleteApiKey(apiKey.getApiKey()));
        apiKeyMapper.deleteByIds(idList);
    }

    /**
     * 根据 apiKey 查询数据
     *
     * @param apiKey ApiKey 值
     * @return ApiKeyModel，查不到返回 null
     */
    @Override
    public ApiKeyModel getApiKeyModel(String apiKey) {
        try {
            ApiKeyResponse response = this.getByApiKey(apiKey);
            return BeanUtil.copyProperties(response, ApiKeyModel.class);
        } catch (SoException e) {
            return null;
        }
    }
}
