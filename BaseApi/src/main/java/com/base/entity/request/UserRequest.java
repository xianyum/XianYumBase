package com.base.entity.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/***
 * 获取登录提交的参数
 */
@Data
public class UserRequest extends BaseRequest {

    private String captcha;
    private String uuid;
    private Long id;
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
    private Integer delTag;//删除标记
    private String captchaVerification;
    private Integer permission;
}
