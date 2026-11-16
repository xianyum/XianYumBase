package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.request.ApiKeyRequest;
import cn.xianyum.system.entity.response.ApiKeyResponse;

import java.util.List;

/**
 * API Key 服务
 */
public interface ApiKeyService {

    PageResponse<ApiKeyResponse> getPage(ApiKeyRequest request);

    /**
     * 获取授权范围下拉列表，数据来源于接口上的 @SaCheckApiKey(scope = "...")
     */
    List<String> getScopeList();

    ApiKeyResponse getById(Long id);

    ApiKeyResponse getByApiKey(String apiKey);

    int save(ApiKeyRequest request);

    int update(ApiKeyRequest request);

    void deleteByIds(Long[] ids);
}
