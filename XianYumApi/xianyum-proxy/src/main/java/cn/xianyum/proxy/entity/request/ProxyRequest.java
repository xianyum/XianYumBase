package cn.xianyum.proxy.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

/**
 * 客户端管理(proxy)
 *
 */
@Data
public class ProxyRequest extends BaseRequest {


    /** 客户端秘钥 */
    private String id;

    /** 客户端名称 */
    private String name;

    /** 客户端通知email */
    private String notifyEmail;

    /** 是否通知，0：不通知 1：通知 */
    private Integer notify;
}
