package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 消息发送配置(message_send_config)
 *
 */
@Data
@TableName(value = "message_send_config")
public class MessageSendConfigEntity extends BaseEntity {

    /** id */
	@TableId(type = IdType.INPUT)
    private String id;

    /** 关联消息code(message_config.message_code) */
    private String messageCode;

    /** 发送标题 */
    private String messageTitle;

    /** 是否启用(0:启用 1：禁用) */
    private Integer status;

    /** 限制发送开始时间（期间内不能发送消息） */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Date limitSendStartTime;

    /** 限制发送结束时间（期间内不能发送消息） */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Date limitSendEndTime;
}
