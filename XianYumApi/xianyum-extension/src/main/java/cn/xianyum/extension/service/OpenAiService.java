package cn.xianyum.extension.service;

import cn.xianyum.common.entity.ai.OpenAiLogResponse;
import cn.xianyum.common.entity.ai.OpenAiModelResponse;
import cn.xianyum.common.entity.ai.OpenAiTokenUsageResponse;

import java.util.List;

/**
 * @author xianyum
 * @date 2026/5/8 21:03
 */
public interface OpenAiService {

    /**
     * 获取模型列表
     * @return
     */
    List<OpenAiModelResponse> getModels();

    /**
     * 获取令牌使用情况
     * @return
     */
    OpenAiTokenUsageResponse getTokenUsage();

    /**
     * 获取令牌日志
     * @return
     */
    List<OpenAiLogResponse> getTokenLog();
}
