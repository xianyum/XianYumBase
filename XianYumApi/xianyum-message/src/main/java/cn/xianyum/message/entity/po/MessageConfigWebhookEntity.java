package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 账户配置webhook(message_config_webhook)
 *
 */
@Data
@TableName(value = "message_config_webhook")
public class MessageConfigWebhookEntity extends BaseEntity {

    /** id */
	@TableId(type = IdType.INPUT)
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
