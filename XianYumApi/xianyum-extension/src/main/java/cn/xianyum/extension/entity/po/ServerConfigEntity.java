package cn.xianyum.extension.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 主机维护(ServerConfig)表实体类
 *
 * @author zhangwei
 * @since 2024-04-02 22:27:34
 */
@Data
@TableName("server_config")
public class ServerConfigEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
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

    /**
     * 服务器位置
     */
    private String serverLocation;
}
