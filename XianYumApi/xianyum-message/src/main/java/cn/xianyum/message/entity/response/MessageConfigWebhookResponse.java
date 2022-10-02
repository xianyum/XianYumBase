package cn.xianyum.message.entity.response;

import lombok.Data;
import java.util.Date;

/**
 * 账户配置webhook(message_config_webhook)
 *
 */
@Data
public class MessageConfigWebhookResponse{

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

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;


}
