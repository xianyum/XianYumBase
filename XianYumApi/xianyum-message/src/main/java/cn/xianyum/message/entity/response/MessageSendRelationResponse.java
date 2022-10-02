package cn.xianyum.message.entity.response;

import lombok.Data;

import java.util.Date;

/**
 * 发送配置关联表(message_send_relation)
 *
 */
@Data
public class MessageSendRelationResponse{

    /** id */
    private String id;

    /** 关联发送表id（message_config_send.id） */
    private String messageSendId;

    /** 消息账户类型 */
    private String messageAccountType;

    /** 关联账户配置id */
    private String messageConfigId;

    /** 账户配置描述 */
    private String messageConfigDescription;

    /** 发送用户，多个用|隔开 */
    private String toUser;

}
