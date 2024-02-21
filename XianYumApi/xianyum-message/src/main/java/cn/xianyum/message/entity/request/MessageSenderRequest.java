package cn.xianyum.message.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2024/2/21 14:58
 */
@Data
public class MessageSenderRequest extends BaseRequest {

    /** 发送标题 */
    @NotBlank(message = "消息标题不能为空！")
    private String title;

    /** 发送内容，优先发送内容 */
    @NotBlank(message = "消息内容不能为空！")
    private String content;

    /** 消息编码 */
    @NotBlank(message = "消息编码不能为空！")
    private String messageCode;

    /** 跳转链接 */
    private String formUrl;

    /** 发送微信用户 */
    private String wechatToUser;

    /** 发送邮箱用户 */
    private String emailToUser;
}
