package cn.xianyum.common.entity.ai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author xianyum
 * @date 2026/5/7 22:43
 */
@Data
public class OpenAiLogResponse {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    @JSONField(name = "user_id")
    private Long userId;

    /**
     * 创建时间戳
     */
    @JSONField(name = "created_at")
    private Long createdAt;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 内容
     */
    private String content;

    /**
     * 用户名
     */
    private String username;

    /**
     * 令牌名称
     */
    @JSONField(name = "token_name")
    private String tokenName;

    /**
     * 模型名称
     */
    @JSONField(name = "model_name")
    private String modelName;

    /**
     * 配额
     */
    private Long quota;

    /**
     * 提示词令牌数
     */
    @JSONField(name = "prompt_tokens")
    private Long promptTokens;

    /**
     * 完成令牌数
     */
    @JSONField(name = "completion_tokens")
    private Long completionTokens;

    /**
     * 使用时间（毫秒）
     */
    @JSONField(name = "use_time")
    private Integer useTime;

    /**
     * 是否流式
     */
    @JSONField(name = "is_stream")
    private Boolean isStream;

    /**
     * 渠道
     */
    private Integer channel;

    /**
     * 渠道名称
     */
    @JSONField(name = "channel_name")
    private String channelName;

    /**
     * 令牌ID
     */
    @JSONField(name = "token_id")
    private Long tokenId;

    /**
     * 分组
     */
    private String group;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求ID
     */
    @JSONField(name = "request_id")
    private String requestId;

    /**
     * 其他信息（JSON字符串）
     */
    private String other;
}
