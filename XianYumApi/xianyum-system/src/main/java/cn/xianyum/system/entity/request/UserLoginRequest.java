package cn.xianyum.system.entity.request;

import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.system.entity.dto.QqUserInfoDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author xianyum
 * @date 2026/2/1 21:52
 */
@Data
public class UserLoginRequest {

    /** 账号 */
    @NotBlank(message = "账号/邮箱号不能为空")
    private String username;

    /** 密码 */
    private String password;

    /** 验证码 */
    private String verifyCode;

    /** 登录凭证 */
    private String code;

    /** 临时授权码 */
    private String authCode;

    /** token */
    private String accessToken;

    /** openId */
    private String openId;

    /** qq用户信息 */
    private QqUserInfoDto qqUserInfo;

    /** 登录类型 */
    private LoginTypeEnum loginType = LoginTypeEnum.USER_PASSWORD;
}

