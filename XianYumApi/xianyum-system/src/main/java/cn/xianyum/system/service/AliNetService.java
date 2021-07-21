package cn.xianyum.system.service;

import com.alipay.api.response.AlipayUserInfoShareResponse;

/**
 * 阿里相关业务
 * @author zhangwei
 * @date 2019/11/9 17:06
 * @desc
 */
public interface AliNetService {

    /**
     * 获取token
     * @param authCode
     * @return
     */
    String getAccessToken(String authCode);

    /**
     * 根据token获取支付宝用户信息
     * @param accessToken
     * @return
     */
    AlipayUserInfoShareResponse getALiUserInfo(String accessToken);
}
