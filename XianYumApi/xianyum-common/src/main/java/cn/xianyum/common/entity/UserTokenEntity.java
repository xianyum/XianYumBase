package cn.xianyum.common.entity;

import lombok.Data;
import java.util.Date;

/***
 * 用户信息
 */
@Data
public class UserTokenEntity {

    private String id;

    private String username;

    private String password;

    private String mobile;

    private String email;

    private Integer status;//状态吗  1：允许登录 0：禁止登录

    private Date createTime;//创建时间

    private Integer delTag;//删除标记

    private Integer permission;

    private Integer sex;

    private String avatar;


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
     * 登录系统（用来区分外部系统）
     */
    private String loginSystem;

    /**
     * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
     */
    private int loginType;
}
