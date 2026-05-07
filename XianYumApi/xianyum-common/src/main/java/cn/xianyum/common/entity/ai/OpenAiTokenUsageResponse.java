package cn.xianyum.common.entity.ai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Map;

/**
 * 令牌使用情况实体
 * @author zhangwei
 * @date 2026/5/7
 */
@Data
public class OpenAiTokenUsageResponse {

    /**
     * 过期时间
     */
    @JSONField(name = "expires_at")
    private Long expiresAt;

    /**
     * 模型限制
     */
    @JSONField(name = "model_limits")
    private Map<String, Object> modelLimits;

    /**
     * 是否启用模型限制
     */
    @JSONField(name = "model_limits_enabled")
    private Boolean modelLimitsEnabled;

    /**
     * 名称
     */
    private String name;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 总可用量
     */
    @JSONField(name = "total_available")
    private Long totalAvailable;

    /**
     * 总授权量
     */
    @JSONField(name = "total_granted")
    private Long totalGranted;

    /**
     * 总使用量
     */
    @JSONField(name = "total_used")
    private Long totalUsed;

    /**
     * 是否无限配额
     */
    @JSONField(name = "unlimited_quota")
    private Boolean unlimitedQuota;
}
