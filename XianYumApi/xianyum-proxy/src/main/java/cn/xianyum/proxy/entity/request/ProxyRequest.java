package cn.xianyum.proxy.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

/**
 * 客户端管理(proxy)
 *
 */
@Data
public class ProxyRequest extends BaseRequest {


    /** 客户端秘钥 */
    private String id;

    /** 客户端名称 */
    private String name;

    /** 客户端通知email */
    private String notifyEmail;

    /** 是否通知，0：不通知 1：通知 */
    private Integer notify;

    /** mac地址 */
    private String macAddress;

    /** hostIp地址 */
    private String hostIp;

    /** 主机名 */
    private String hostName;

    /** 操作系统 */
    private String osName;

    /** 客户端版本号 */
    private Double clientVersion;

    /** 当前读取量 */
    private Long readBytes;

    /** 当前读取量 */
    private Long writeBytes;
}
