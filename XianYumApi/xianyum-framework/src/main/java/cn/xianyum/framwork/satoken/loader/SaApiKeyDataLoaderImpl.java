package cn.xianyum.framwork.satoken.loader;

import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.system.entity.response.ApiKeyResponse;
import cn.xianyum.system.service.ApiKeyService;
import org.springframework.stereotype.Component;

/**
 * API Key 数据加载器实现类 （从数据库查询）
 * @author xianyum
 * @date 2026/11/15 20:10
 */
@Component
public class SaApiKeyDataLoaderImpl implements SaApiKeyDataLoader {


    // 指定框架不再维护 API Key 索引信息，而是由我们手动从数据库维护
    @Override
    public Boolean getIsRecordIndex() {
        return false;
    }

    // 根据 apiKey 从数据库获取 ApiKeyModel 信息 （实现此方法无需为数据做缓存处理，框架内部已包含缓存逻辑）
    @Override
    public ApiKeyModel getApiKeyModelFromDatabase(String namespace, String apiKey) {
        ApiKeyResponse apiKeyResponse = SpringUtil.getBean(ApiKeyService.class).getByApiKey(apiKey);
        return BeanUtil.copyProperties(apiKeyResponse, ApiKeyModel.class);
    }
}
