package cn.xianyum.proxy.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 客户端配置详细(proxy_details)
 *
 */
@Data
public class ProxyDetailsRequest extends BaseRequest {


    /** id */
    private String id;

    /** 客户端秘钥 */
    @NotBlank(message="客户端不能为空")
    private String proxyId;

    /** 客户端名称 */
    private String proxyName;

    /** 映射外网端口 */
    private Integer inetPort;

    /** 代理信息(ip:port) */
    @NotBlank(message="代理地址不能为空")
    private String lan;

    /** 代理名称 */
    @NotBlank(message="代理名称不能为空")
    private String name;

}
