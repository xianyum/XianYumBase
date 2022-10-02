package cn.xianyum.message.entity.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 消息发送配置(message_send_config)
 *
 */
@Data
public class MessageSendConfigResponse{

    /** id */
    private String id;

    /** 关联消息code(message_config.message_code) */
    private String messageCode;

    /** 消息配置描述 */
    private String messageTypeDescription;

    /** 发送标题 */
    private String messageTitle;

    /** 是否启用(0:启用 1：禁用) */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 限制发送开始时间（期间内不能发送消息） */
    private Date limitSendStartTime;

    /** 限制发送结束时间（期间内不能发送消息） */
    private Date limitSendEndTime;

    /** 发送详情关系 */
    private List<MessageSendRelationResponse> messageSendRelationResponses;
}
