package cn.xianyum.proxy.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;
import java.util.Date;

/**
 * 客户端管理(proxy)
 *
 */
@Data
public class ProxyResponse extends BaseResponse {

    /** 客户端秘钥 */
    private String id;

    /** 客户端名称 */
    private String name;

    /** 创建时间 */
    private Date createTime;

    /** 登录次数 */
    private Integer loginCount;

    /** 当前登录状态  0:不在线 1：在线*/
    private Integer status;

    /** 客户端通知email */
    private String notifyEmail;

    /** 是否通知，0：不通知 1：通知 */
    private Integer notify;

}
