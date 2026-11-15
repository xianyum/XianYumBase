package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import cn.xianyum.common.handler.StringToListTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * API Key 调用密钥
 */
@Data
@TableName("api_key")
public class ApiKeyEntity extends BaseEntity {

    /** 主键ID */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** ApiKey 值，全局唯一，如 AK-xxxx */
    private String apiKey;

    /** 名称 */
    private String title;

    /** 介绍/用途描述 */
    private String intro;

    /** 归属账号ID，对应 user.id */
    private String loginId;

    /** 授权范围，英文逗号分隔，如 userinfo,chat */
    @TableField(typeHandler = StringToListTypeHandler.class)
    private List<String> scopes;

    /** 到期时间，13位毫秒时间戳，-1=永不过期 */
    private Long expiresTime;

    /** 是否有效：1生效 0禁用 */
    @TableField("is_valid")
    private Boolean isValid;
}
