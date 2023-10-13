package cn.xianyum.message.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 发送配置关联表(message_send_relation)
 *
 */
@Data
public class MessageSendRelationRequest extends BaseRequest {


    /** id */
    private String id;

    /** 关联发送表id（message_config_send.id） */
    private String messageSendId;

    /** 消息类型 */
    private String messageAccountType;

    /** 关联账户配置id */
    private String messageConfigId;

    /** 发送用户，多个用|隔开 */
    private String toUser;


}
