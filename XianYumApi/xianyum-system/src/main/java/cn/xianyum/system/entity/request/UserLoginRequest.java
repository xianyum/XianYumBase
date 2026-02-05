package cn.xianyum.system.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author xianyum
 * @date 2026/2/1 21:52
 */
@Data
public class UserLoginRequest {

    /** 账号 */
    @NotBlank(message = "账号不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}

