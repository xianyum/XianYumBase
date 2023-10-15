package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/***
 * 用户信息
 */
@Data
@TableName(value = "user")
public class UserEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String username;

    private String password;

    private String mobile;

    private String email;

    private Integer status;

    private Integer delTag;

    private Integer permission;

    private Integer sex;

    private String avatar;

    /**
     * 登录时间
     */
    @TableField(exist = false)
    private Date loginTime;

    /**
     * 登录IP地址
     */
    @TableField(exist = false)
    private String ipaddr;

    /**
     * 登录地点
     */
    @TableField(exist = false)
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField(exist = false)
    private String browser;

    /**
     * 操作系统
     */
    @TableField(exist = false)
    private String os;

    /**
     * 登录系统（用来区分外部系统）
     */
    @TableField(exist = false)
    private String loginSystem;

    /**
     * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
     */
    @TableField(exist = false)
    private int loginType;
}
