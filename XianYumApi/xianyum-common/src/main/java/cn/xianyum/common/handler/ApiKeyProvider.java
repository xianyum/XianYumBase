package cn.xianyum.common.handler;

import cn.dev33.satoken.apikey.model.ApiKeyModel;

/**
 * API Key 数据提供者
 * 由业务模块实现，框架模块只面向此接口编程
 */
public interface ApiKeyProvider {

    /**
     * 根据 apiKey 查询数据
     *
     * @param apiKey ApiKey 值
     * @return ApiKeyModel，查不到返回 null
     */
    ApiKeyModel getApiKeyModel(String apiKey);
}
