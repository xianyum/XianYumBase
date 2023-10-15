package cn.xianyum.proxy.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 远程代理日志(proxy_log)
 *
 */
@Data
@TableName(value = "proxy_log")
public class ProxyLogEntity extends BaseEntity {

	@TableId(type = IdType.AUTO)
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

}
