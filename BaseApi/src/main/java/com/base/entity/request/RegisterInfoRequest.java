package com.base.entity.request;

import com.base.common.validator.group.AddGroup;
import com.base.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author zhangwei
 * @date 2019/3/19 14:31
 */
@Data
public class RegisterInfoRequest {

    @NotBlank(message="登录账号不能为空" ,groups = {UpdateGroup.class})
    private String username;//登录账号
    @NotBlank(message="登录密码不能为空" ,groups = {UpdateGroup.class})
    private String password;//登录密码
    @NotBlank(message="手机号不能为空" ,groups = {UpdateGroup.class})
    private String mobile;
    @NotBlank(message="邮箱不能为空" ,groups = {AddGroup.class, UpdateGroup.class})
    @Email(message="邮箱格式不正确" ,groups = {AddGroup.class, UpdateGroup.class})
    private String email;
    private String userSource;//用户来源
    @NotBlank(message="验证码不能为空" ,groups = {UpdateGroup.class})
    private String code;
    @NotBlank(message="uuid不能为空" ,groups = {AddGroup.class, UpdateGroup.class})
    private String uuid;
    private String authCode;
}
