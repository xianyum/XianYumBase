package cn.xianyum.message.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;
import java.util.Date;

/**
 * 账户配置wechat(message_config_wechat)
 *
 */
@Data
public class MessageConfigWechatRequest extends BaseRequest {


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

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 删除标志（0：未删除，1：删除） */
    private Integer delTag;


}
