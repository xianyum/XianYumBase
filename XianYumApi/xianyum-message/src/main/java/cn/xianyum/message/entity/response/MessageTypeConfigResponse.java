package cn.xianyum.message.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 消息类型配置表(message_type_config)
 *
 */
@Data
public class MessageTypeConfigResponse extends BaseResponse {

    /** id */
    private String id;

    /** 消息编码 */
    private String messageCode;

    /** 消息描述 */
    private String description;

    /** 发送量 */
    private Integer sendCount;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;

    /** echarts显示（0：隐藏 ，1：显示） */
    private Integer echartsTag;

}
