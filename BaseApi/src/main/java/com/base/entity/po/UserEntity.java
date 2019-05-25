package com.base.entity.po;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/***
 * 用户信息
 */
@Data
public class UserEntity extends PagePo{
    private Integer id;
    @NotBlank(message="账号不能为空")
    private String username;//登录账号
    private String password;//登录密码
    private String salt;//盐
    @NotBlank(message="手机号不能为空")
    private String mobile;
    @NotBlank(message="邮箱不能为空")
    @Email(message="邮箱格式不正确")
    private String email;
    private Integer status;//状态吗  1：允许登录 0：禁止登录
    private String userSource;//用户来源
    private Date createTime;//创建时间
    private Integer permissionStatus; //权限状态标识
    private String permissionName;
    private Integer delTag;//删除标记
    private String nickName;
    private Integer sex;
    private String department;
    private String major;
}
