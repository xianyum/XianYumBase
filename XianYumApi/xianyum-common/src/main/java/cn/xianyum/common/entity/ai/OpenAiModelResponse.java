package cn.xianyum.common.entity.ai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * OpenAI 模型实体
 * @author zhangwei
 * @date 2026/5/7
 */
@Data
public class OpenAiModelResponse {

    /**
     * 模型ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 所有者
     */
    @JSONField(name = "owned_by")
    private String ownedBy;

    /**
     * 支持的端点类型
     */
    @JSONField(name = "supported_endpoint_types")
    private List<String> supportedEndpointTypes;
}
