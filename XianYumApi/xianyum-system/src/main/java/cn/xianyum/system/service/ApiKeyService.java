package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.request.ApiKeyRequest;
import cn.xianyum.system.entity.response.ApiKeyResponse;

/**
 * API Key 服务
 */
public interface ApiKeyService {

    PageResponse<ApiKeyResponse> getPage(ApiKeyRequest request);

    ApiKeyResponse getById(Long id);

    ApiKeyResponse getByApiKey(String apiKey);

    int save(ApiKeyRequest request);

    int update(ApiKeyRequest request);

    void deleteByIds(Long[] ids);
}
