package cn.xianyum.system.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * 在线用户实体
 * @author zhangwei
 * @date 2021/1/28 22:00
 */
@Data
public class UserOnlineEntity {

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
     * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
     */
    private String accountType;
}
