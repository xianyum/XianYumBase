package cn.xianyum.system.entity.request;

import lombok.Data;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2024/3/5 11:12
 */
@Data
public class ThirdOauthRequest {

    /** 临时授权码 */
    private String authCode;

    /** token */
    private String accessToken;

    /** openId */
    private String openId;
}
