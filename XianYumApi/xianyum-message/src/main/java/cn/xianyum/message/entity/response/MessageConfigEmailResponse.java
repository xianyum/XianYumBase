package cn.xianyum.message.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 账户配置email(message_config_email)
 *
 */
@Data
public class MessageConfigEmailResponse extends BaseResponse {

    /** id */
    private String id;

    /** smtp地址 */
    private String emailSmtp;

    /** 邮箱账号 */
    private String emailUserName;

    /** 邮箱密码 */
    private String emailUserPassword;

    /** 描述 */
    private String description;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;

}
