package cn.xianyum.system.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;
import java.util.Date;

/**
 * 在线用户实体
 * @author zhangwei
 * @date 2023年10月17日20:43:05
 */
@Data
public class UserOnlineResponse extends BaseResponse {

    /**
     * 会话编号
     */
    private String token;


    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录来源
     */
    private String loginType;
}
