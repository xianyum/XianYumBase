package cn.xianyum.message.entity.po;

import lombok.Data;

/**
 * 消息体
 * @author zhangwei
 * @date 2021/11/21 15:38
 */
@Data
public class MessageContent {

    /** label */
    private String label;

    /** 含义 */
    private String value;
}
