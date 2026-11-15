package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

import java.util.List;

/**
 * API Key 请求参数
 */
@Data
public class ApiKeyRequest extends BaseRequest {

    /** 主键ID，更新时必填 */
    private Long id;

    /** ApiKey 值，新增时为空则由系统生成 */
    private String apiKey;

    /** 名称 */
    private String title;

    /** 介绍/用途描述 */
    private String intro;

    /** 归属账号ID，对应 user.id */
    private String loginId;

    /** 授权范围 */
    private List<String> scopes;

    /** 到期时间，13位毫秒时间戳，-1=永不过期 */
    private Long expiresTime;

    /** 是否有效：true生效 false禁用 */
    private Boolean isValid;
}
