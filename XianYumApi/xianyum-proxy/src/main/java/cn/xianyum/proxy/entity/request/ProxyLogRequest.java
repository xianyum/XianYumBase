package cn.xianyum.proxy.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 远程代理日志(proxy_log)
 *
 */
@Data
public class ProxyLogRequest extends BaseRequest {


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

    private String memoryInfo;

    private Double cpuUseAge;

    private String cpuModel;
}
