package cn.xianyum.system.entity.response;

import lombok.Data;

/**
 * 登录token响应体
 * @author xianyum
 * @date 2026/2/6 21:02
 */
@Data
public class LoginTokenResponse {

    /** 验证码 */
    private String token;

    /** token剩余时间，单位：秒 */
    private long expireTime;
}
