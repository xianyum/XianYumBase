package cn.xianyum.message.entity.response;

import lombok.Data;
import java.util.Date;

/**
 * 消息监控(message_monitor)
 *
 */
@Data
public class MessageMonitorResponse{

    /** id */
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

    /** 发送时间 */
    private Date createTime;


}
