package cn.xianyum.extension.entity.response;

import java.util.Date;
import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 主机维护(ServerConfig)response返回实体
 *
 * @author makejava
 * @since 2024-04-02 22:27:34
 */
@Data
public class ServerConfigResponse extends BaseResponse {
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
