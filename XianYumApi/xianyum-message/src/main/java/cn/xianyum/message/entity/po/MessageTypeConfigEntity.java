package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 消息类型配置表(message_type_config)
 *
 */
@Data
@TableName(value = "message_type_config")
public class MessageTypeConfigEntity extends BaseEntity {

    /** id */
    @TableId(type = IdType.INPUT)
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
