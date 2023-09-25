package cn.xianyum.message.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;
import java.util.Date;

/**
 * 消息类型配置表(message_type_config)
 *
 */
@Data
public class MessageTypeConfigRequest extends BaseRequest {

    /** id */
    private String id;

    /** 消息编码 */
    private String messageCode;

    /** 消息描述 */
    private String description;

    /** 发送量 */
    private Integer sendCount;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;

    /** echarts显示（0：隐藏 ，1：显示） */
    private Integer echartsTag;

}
