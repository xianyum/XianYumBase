package cn.xianyum.message.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 账户配置wechat(message_config_wechat)
 *
 */
@Data
@TableName(value = "message_config_wechat")
public class MessageConfigWechatEntity extends BaseEntity {

    /** id */
    private String id;

    /** 企业id */
    private String corpId;

    /** 应用秘钥 */
    private String corpSecret;

    /** 应用id */
    private String agentId;

    /** 描述 */
    private String description;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;

}
