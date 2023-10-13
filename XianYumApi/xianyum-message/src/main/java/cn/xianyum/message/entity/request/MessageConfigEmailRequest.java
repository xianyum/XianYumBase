package cn.xianyum.message.entity.request;


import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;
import java.util.Date;

/**
 * 账户配置email(message_config_email)
 *
 */
@Data
public class MessageConfigEmailRequest extends BaseRequest {


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

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;


}
