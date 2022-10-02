package cn.xianyum.message.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;
import java.util.Date;

/**
 * 消息监控(message_monitor)
 *
 */
@Data
public class MessageMonitorRequest extends BaseRequest {


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

    private Date startTime;

    private Date endTime;

}
