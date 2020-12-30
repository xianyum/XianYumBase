package com.base.service.iservice;

import com.base.entity.po.QqUserEntity;

/**
 * @author zhangwei
 * @date 2019/11/22 16:58
 */
public interface QqNetService {
    /**
     * 获取QQ用户token
     * @param authCode
     * @return
     */
    String getAccessToken(String authCode);

    /**
     * 获取QQ用户id
     * @param accessToken
     * @return
     */
    QqUserEntity getUserId(String accessToken);

}
