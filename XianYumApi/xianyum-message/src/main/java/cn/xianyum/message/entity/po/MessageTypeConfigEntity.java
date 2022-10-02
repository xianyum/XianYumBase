package cn.xianyum.message.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 消息类型配置表(message_type_config)
 *
 */
@Data
@TableName(value = "message_type_config")
public class MessageTypeConfigEntity{

    /** id */
    @TableId(type = IdType.INPUT)
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

}