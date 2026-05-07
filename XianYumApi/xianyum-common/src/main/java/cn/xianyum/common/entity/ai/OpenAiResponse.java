package cn.xianyum.common.entity.ai;

import lombok.Data;

/**
 * OpenAI 通用响应类
 * 支持 data 为对象或 List<T>
 * @author zhangwei
 * @date 2026/5/7
 */
@Data
public class OpenAiResponse<T> {

    /**
     * 是否成功
     */
    private Boolean code;

    /**
     * 响应数据（支持对象或列表）
     */
    private T data;

    /**
     * 响应消息
     */
    private String message;
}
