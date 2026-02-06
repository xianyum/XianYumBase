package cn.xianyum.system.entity.response;

import lombok.Data;
import java.util.Date;

/**
 * 登录token响应体
 * @author xianyum
 * @date 2026/2/6 21:02
 */
@Data
public class LoginTokenResponse {

    /** 验证码 */
    private String token;

    /** 过期时间 */
    private Date expireTime;
}
