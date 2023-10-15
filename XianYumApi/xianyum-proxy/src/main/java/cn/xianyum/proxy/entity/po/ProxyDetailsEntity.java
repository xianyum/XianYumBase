package cn.xianyum.proxy.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 客户端配置详细(proxy_details)
 *
 */
@Data
@TableName(value = "proxy_details")
public class ProxyDetailsEntity extends BaseEntity {

    /** id */
	@TableId(type = IdType.INPUT)
    private String id;

    /** 客户端秘钥 */
    private String proxyId;

    /** 映射外网端口 */
    private Integer inetPort;

    /** 代理信息(ip:port) */
    private String lan;

    /** 代理名称 */
    private String name;

    /** 当前读取量 */
    private Long readBytes;

    /** 当前读取量 */
    private Long writeBytes;
}
