package cn.xianyum.proxy.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 远程代理日志(proxy_log)
 *
 */
@Data
public class ProxyLogResponse extends BaseResponse {

    private Long id;

    private String proxyId;

    private String macAddress;

    private String hostIp;

    private String hostName;

    private String osName;

    private Double clientVersion;

    private String userDir;

    private String computerName;

    private String computerUserName;

    /** 客户端名称 */
    private String proxyName;

    private String onlineTime;

}
