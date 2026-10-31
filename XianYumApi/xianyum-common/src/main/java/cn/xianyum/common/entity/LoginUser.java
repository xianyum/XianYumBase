package cn.xianyum.common.entity;

import cn.xianyum.common.enums.DataScopeEnum;
import lombok.Data;
import java.util.Date;
import java.util.Set;

/***
 * 用户信息
 */
@Data
public class LoginUser {

    /**
     * 用户id
     */
    private String id;

    /**
     * 三方用户ID
     */
    private String thirdUserId;


    /**
     * 登录名
     */
    private String username;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱号
     */
    private String email;

    /**
     * 状态码，1：允许登录 0：禁止登录
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 删除标记
     */
    private Integer delTag;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 图像地址id
     */
    private String avatarFileId;

    /**
     * 图像地址
     */
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
     * 角色编码
     */
    private Set<String> roles;

    /**
     * 权限标识符
     */
    private Set<String> permissions;

    /**
     * 角色对应的数据权限标识符
     */
    private DataScopeEnum dataScopeEnum;

    /**
     * 登录来源
     */
    private String loginType;


    /**
     * 登录的token
     */
    private String token;
}
