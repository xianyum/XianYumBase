package cn.xianyum.extension.entity.response;

import java.util.Date;
import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 主机端口维护(ServerPortConfig)response返回实体
 *
 * @author zhangwei
 * @since 2024-04-02 22:27:45
 */
@Data
public class ServerPortConfigResponse extends BaseResponse {
    private Long id;

    /**
     * 主机id
     */
    private Long serverConfigId;

    /**
     * 服务器名
     */
    private String serverName;

    /**
     * 端口
     */
    private String port;

    /**
     * 使用备注
     */
        private String remark;

    /**
     * 访问url,多个逗号分割开
     */
    private String accessUrl;

    /**
     * 启动命令
     */
    private String startCommand;

}
