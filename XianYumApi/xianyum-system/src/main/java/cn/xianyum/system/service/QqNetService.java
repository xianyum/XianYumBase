package cn.xianyum.system.service;


import cn.xianyum.system.entity.dto.QqUserInfoDto;

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
     *
     * @param accessToken
     * @param qqUserInfo
     * @return
     */
    QqUserInfoDto getUserId(String accessToken, QqUserInfoDto qqUserInfo);

}
