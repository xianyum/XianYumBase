package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.ai.OpenAiLogResponse;
import cn.xianyum.common.entity.ai.OpenAiModelResponse;
import cn.xianyum.common.entity.ai.OpenAiTokenUsageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.service.OpenAiService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * OpenAI 控制器
 * @author zhangwei
 * @date 2026/5/8
 */
@RestController
@RequestMapping("xym-extension/v1/openai")
public class OpenAiController {

    @Resource
    private OpenAiService openAiService;

    /**
     * 获取 OpenAI 模型列表
     * @return 模型列表
     */
    @GetMapping("/models")
    public Results<List<OpenAiModelResponse>> getModels() {
        return Results.success(openAiService.getModels());
    }

    /**
     * 获取令牌使用情况
     * @return 令牌使用情况
     */
    @GetMapping("/token/usage")
    @Permission(publicApi = true)
    public Results<OpenAiTokenUsageResponse> getTokenUsage() {
        return Results.success(openAiService.getTokenUsage());
    }

    /**
     * 获取令牌日志
     * @return 令牌日志列表
     */
    @GetMapping("/token/log")
    public Results<List<OpenAiLogResponse>> getTokenLog() {
        return Results.success(openAiService.getTokenLog());
    }
}
