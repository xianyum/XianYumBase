package com.base.entity.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/***
 * 获取请求的新密码和旧密码
 */
@Data
public class UpdatePasswordRequest {
    @NotBlank(message="原密码不能为空")
    private String password;
    @NotBlank(message="新密码不能为空")
    private String newPassword;//新密码
}
