package cn.xianyum.extension.entity.response;

import lombok.Data;

/**
 * OTP网络验证响应
 */
@Data
public class OtpNetworkAuthResponse {

    /**
     * 主键ID
     */
    private String id;

    /**
     * OTP认证URI
     */
    private String otpAuthUri;

}
