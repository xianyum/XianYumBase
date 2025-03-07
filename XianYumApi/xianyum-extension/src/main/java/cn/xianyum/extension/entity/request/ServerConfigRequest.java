package cn.xianyum.extension.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 主机维护(ServerConfig)request请求实体
 *
 * @author makejava
 * @since 2024-04-02 22:27:34
 */
@Data
public class ServerConfigRequest extends BaseRequest {
    private Long id;

    /**
     * 公网ip
     */
    private String serverPublicIp;

    /**
     * 内网ip
     */
    private String serverLanIp;

    /**
     * 服务器名
     */
    private String serverName;

    /**
     * 标签
     */
    private String tag;

    /**
     * 内存总量
     */
    private String memTotal;

    /**
     * cpu核心数
     */
    private String cpuNum;

    /**
     * 使用备注
     */
    private String useRemark;

}
