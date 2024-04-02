package cn.xianyum.extension.entity.po;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 主机端口维护(ServerPortConfig)表实体类
 *
 * @author makejava
 * @since 2024-04-02 22:27:45
 */
@Data
@TableName("server_port_config")
public class ServerPortConfigEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主机id
     */
    private Long serverConfigId;

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
