package cn.xianyum.message.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 账户配置webhook(message_config_webhook)
 *
 */
@Data
public class MessageConfigWebhookResponse extends BaseResponse {

    /** id */
    private String id;

    /** webHook地址 */
    private String webHookUrl;

    /** 秘钥 */
    private String webHookSecret;

    /** 消息类型（取系统常用变量里） */
    private String messageAccountType;

    /** 描述 */
    private String description;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;
}
