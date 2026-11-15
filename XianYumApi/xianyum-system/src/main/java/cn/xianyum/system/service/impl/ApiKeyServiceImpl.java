package cn.xianyum.system.service.impl;

import cn.dev33.satoken.apikey.template.SaApiKeyUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.IdGeneratorUtil;
import cn.xianyum.system.dao.ApiKeyMapper;
import cn.xianyum.system.entity.po.ApiKeyEntity;
import cn.xianyum.system.entity.request.ApiKeyRequest;
import cn.xianyum.system.entity.response.ApiKeyResponse;
import cn.xianyum.system.service.ApiKeyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Objects;

/**
 * API Key 服务实现
 */
@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private ApiKeyMapper apiKeyMapper;

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
        return PageResponse.of(pageResult, ApiKeyResponse.class);
    }

    @Override
    public ApiKeyResponse getById(Long id) {
        ApiKeyEntity entity = apiKeyMapper.selectById(id);
        if (Objects.isNull(entity)) {
            throw new SoException("API Key 不存在");
        }
        return BeanUtil.copyProperties(entity, ApiKeyResponse.class);
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
        Long count = apiKeyMapper.selectCount(
                new QueryWrapper<ApiKeyEntity>().eq("api_key", apiKey));
        if (count > 0) {
            throw new SoException("ApiKey 已存在");
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
        if (Objects.isNull(apiKeyMapper.selectById(request.getId()))) {
            throw new SoException("API Key 不存在");
        }
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(request.getId());
        entity.setTitle(request.getTitle());
        entity.setIntro(request.getIntro());
        entity.setScopes(request.getScopes());
        entity.setExpiresTime(request.getExpiresTime());
        entity.setIsValid(request.getIsValid());
        return apiKeyMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new SoException("请选择要删除的数据");
        }
        apiKeyMapper.deleteByIds(Arrays.asList(ids));
    }
}
