package cn.xianyum.message.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 发送配置关联表(message_send_relation)
 *
 */
@Data
@TableName(value = "message_send_relation")
public class MessageSendRelationEntity{

    /** id */
	@TableId(type = IdType.INPUT)
    private String id;

    /** 关联发送表id（message_config_send.id） */
    private String messageSendId;

    /** 消息账户类型 */
    private String messageAccountType;

    /** 关联账户配置id */
    private String messageConfigId;

    /** 发送用户，多个用|隔开 */
    private String toUser;

    private Date createTime;
}
