package cn.xianyum.message.entity.po;

import lombok.Data;
import java.util.List;

/**
 * @author zhangwei
 * @date 2021/11/21 15:27
 */
@Data
public class MessageSenderEntity {

    /** 消息ID */
    private String messageId;

    /** 发送标题 */
    private String title;

    /**
     * 默认发送用户
     */
    private String defaultToUser;

    /** 发送微信用户 */
    private String wechatToUser;

    /** 发送邮箱用户 */
    private String emailToUser;

    /** 发送内容，优先发送内容 */
    private String content;

    /** 跳转链接 */
    private String formUrl;

    /** 消息编码 */
    private String messageCode;

    /** 配置编码 */
    private String messageConfigId;

    /** 消息类型 */
    private String messageAccountType;

    /** 邮箱路径 */
    private String emailHtmlPath;

    /** 消息体 */
    List<MessageContent> messageContents;
}
