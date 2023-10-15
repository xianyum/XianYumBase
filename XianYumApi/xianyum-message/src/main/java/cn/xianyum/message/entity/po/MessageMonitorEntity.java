package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 消息监控(message_monitor)
 *
 */
@Data
@TableName(value = "message_monitor")
public class MessageMonitorEntity extends BaseEntity {

    /** id */
	@TableId(type = IdType.INPUT)
    private String id;

    /** 消息编码 */
    private String messageCode;

    /** 消息标题 */
    private String messageTitle;

    /** 消息内容 */
    private String messageContent;

    /** 响应结果 */
    private String messageResponse;

    /** 发送人 */
    private String toUser;

    /** 消息类型 */
    private String messageType;

}
