package cn.xianyum.system.entity.request;

import lombok.Data;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2024/3/5 11:12
 */
@Data
public class ThirdOauthRequest {
    /** 阿里临时授权码 */
    private String authCode;
}
