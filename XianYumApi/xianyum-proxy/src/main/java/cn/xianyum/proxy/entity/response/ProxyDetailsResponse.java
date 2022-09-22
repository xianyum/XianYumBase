package cn.xianyum.proxy.entity.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 客户端配置详细(proxy_details)
 *
 */
@Data
public class ProxyDetailsResponse{

    /** id */
    private String id;

    /** 客户端秘钥 */
    private String proxyId;

    /** 客户端名称 */
    private String proxyName;

    /** 映射外网端口 */
    private Integer inetPort;

    /** 代理信息(ip:port) */
    private String lan;

    /** 代理名称 */
    private String name;

    List<ProxyDetailsResponse> proxyDetailsResponses;

    private Date createTime;

    /** 当前读取量转换可读 */
    private String readBytesStr;

    /** 当前读取量转换可读 */
    private String writeBytesStr;

    /** 当前读取量 */
    private Long readBytes;

    /** 当前读取量 */
    private Long writeBytes;

    /** 当前连接数 */
    private int connectCount;
}
