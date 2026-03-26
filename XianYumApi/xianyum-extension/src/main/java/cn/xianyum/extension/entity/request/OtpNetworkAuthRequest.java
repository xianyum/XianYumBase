package cn.xianyum.extension.entity.request;

import lombok.Data;

/**
 * OTP网络验证请求
 */
@Data
public class OtpNetworkAuthRequest {

    /**
     * 主键ID
     */
    private String id;

    /**
     * OTP认证URI
     */
    private String otpAuthUri;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 状态0-启用1-禁用
     */
    private Integer status;
}
