package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 账户配置email(message_config_email)
 *
 * @author zhangwei
 */
@Data
@TableName(value = "message_config_email")
public class MessageConfigEmailEntity extends BaseEntity {

    /** id */
	@TableId(type = IdType.INPUT)
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
