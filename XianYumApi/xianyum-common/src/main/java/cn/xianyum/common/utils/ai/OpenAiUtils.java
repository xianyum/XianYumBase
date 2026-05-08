package cn.xianyum.common.utils.ai;

import cn.xianyum.common.entity.ai.OpenAiLogResponse;
import cn.xianyum.common.entity.ai.OpenAiModelResponse;
import cn.xianyum.common.entity.ai.OpenAiResponse;
import cn.xianyum.common.entity.ai.OpenAiTokenUsageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.SpringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

/**
 * OpenAI 工具类
 * 封装与 OpenAI 相关的通用操作
 * @author zhangwei
 * @date 2026/5/7
 */
@Slf4j
public class OpenAiUtils {

    private OpenAiUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 向 OpenAI 发送消息并获取响应内容
     *
     * @param prompt 用户输入的提示词
     * @return OpenAI 返回的响应内容
     */
    public static String chat(String prompt) {
        try {
            ChatClient chatClient = SpringUtils.getBean(ChatClient.class);
            return chatClient.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.error("OpenAI 请求失败", e);
            throw new SoException("AI请求失败,失败原因："+e.getMessage());
        }
    }

    /**
     * 向 OpenAI 发送消息并获取响应内容（支持指定模型）
     *
     * @param prompt 用户输入的提示词
     * @param model  要使用的模型名称
     * @return OpenAI 返回的响应内容
     */
    public static String chat(String prompt, String model) {
        try {
            ChatClient chatClient = SpringUtils.getBean(ChatClient.class);
            return chatClient.prompt()
                    .modelName(model)
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("OpenAI 请求失败", e);
            throw new SoException("AI请求失败,失败原因："+e.getMessage());
        }
    }


    /**
     * 获取 OpenAI 模型列表
     * 通过 HTTP 请求调用 OpenAI 的 models API 获取所有可用模型
     *
     * @return 模型列表的 JSON 字符串
     */
    public static List<OpenAiModelResponse> getModels() {
        try {
            // 从配置中获取 OpenAI 的 base-url
            String baseUrl = SpringUtils.getProperty("spring.ai.openai.base-url");
            // 从配置中获取 API Key
            String apiKey = SpringUtils.getProperty("spring.ai.openai.api-key");
            validateConfig(baseUrl, apiKey);

            // 构建 models API 地址
            String modelsUrl = baseUrl + "/v1/models";

            // 使用 HttpUtils 发送 GET 请求，并添加 Authorization header
            String result = HttpUtils.getHttpInstance()
                    .sync(modelsUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .get()
                    .getBody()
                    .toString();
            OpenAiResponse<List<OpenAiModelResponse>> openAiResponse = JSONObject.parseObject(result, new TypeReference<>(){});
            return openAiResponse.getData();
        }catch (Exception e) {
            log.error("获取 OpenAI 模型列表失败", e);
            throw new SoException("获取 AI 模型列表失败,失败原因："+e.getMessage());
        }
    }



    /**
     * 获取令牌日志
     * 通过 HTTP 请求调用 OpenAI 的令牌日志 API
     *
     * @return 令牌日志的 JSON 字符串
     */
    public static List<OpenAiLogResponse> getTokenLog() {
        try {
            String baseUrl = SpringUtils.getProperty("spring.ai.openai.base-url");
            String apiKey = SpringUtils.getProperty("spring.ai.openai.api-key");

            validateConfig(baseUrl, apiKey);

            String tokenLogUrl = baseUrl + "/api/log/token";

            String result = HttpUtils.getHttpInstance()
                    .sync(tokenLogUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .get()
                    .getBody()
                    .toString();

            OpenAiResponse<List<OpenAiLogResponse>> openAiResponse = JSONObject.parseObject(result, new TypeReference<>(){});
            return openAiResponse.getData();
        }catch (Exception e) {
            log.error("获取令牌日志失败", e);
            throw new SoException("获取令牌日志失败,失败原因："+e.getMessage());
        }
    }

    /**
     * 获取令牌使用情况
     * 通过 HTTP 请求调用 OpenAI 的令牌使用情况 API
     *
     * @return 令牌使用情况的 JSON 字符串
     */
    public static OpenAiTokenUsageResponse getTokenUsage() {
        try {
            String baseUrl = SpringUtils.getProperty("spring.ai.openai.base-url");
            String apiKey = SpringUtils.getProperty("spring.ai.openai.api-key");

            validateConfig(baseUrl, apiKey);

            String tokenUsageUrl = baseUrl + "/api/usage/token";

            String result = HttpUtils.getHttpInstance()
                    .sync(tokenUsageUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .get()
                    .getBody()
                    .toString();
            OpenAiResponse<OpenAiTokenUsageResponse> openAiResponse = JSONObject.parseObject(result, new TypeReference<>(){});
            return openAiResponse.getData();
        }catch (Exception e) {
            log.error("获取令牌使用情况失败", e);
            throw new SoException("获取令牌使用情况失败,失败原因："+e.getMessage());
        }
    }


    /**
     * 验证配置
     *
     * @param baseUrl OpenAI base-url
     * @param apiKey  API Key
     */
    private static void validateConfig(String baseUrl, String apiKey) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new SoException("未配置 OpenAI base-url");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new SoException("未配置 OpenAI api-key");
        }
    }

}
