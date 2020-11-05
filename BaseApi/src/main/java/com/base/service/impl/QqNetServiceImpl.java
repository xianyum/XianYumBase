package com.base.service.impl;

import com.base.common.utils.HttpUtils;
import com.base.common.utils.StringUtil;
import com.base.service.iservice.QqNetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 参考文档：https://wiki.connect.qq.com/%E5%87%86%E5%A4%87%E5%B7%A5%E4%BD%9C_oauth2-0
 * @author zhangwei
 * @date 2019/11/22 16:58
 */
@Service
@Slf4j
public class QqNetServiceImpl implements QqNetService {

    /** 获取Access_Token */
    private static final String ACCESS_TOKEN_URL="https://graph.qq.com/oauth2.0/token";
    private static final String CLIENT_ID="101831000";
    private static final String CLIENT_SECRET="5d8b22dd879c4e942d691d0b7b1940e9";
    private static final String GRANT_TYPE="authorization_code";
    private static final String REDIRECT_URI="http://xianyum.cn/#/checkQQLogin";
    /** 获取OpenID */
    private static final String OPEN_ID_URL="https://graph.qq.com/oauth2.0/me";

    /**
     * 获取QQ用户token
     *
     * @param authCode
     * @return
     */
    @Override
    public String getAccessToken(String authCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("grant_type="+GRANT_TYPE);
        stringBuilder.append("&client_id="+CLIENT_ID);
        stringBuilder.append("&client_secret="+CLIENT_SECRET);
        stringBuilder.append("&code="+authCode);
        stringBuilder.append("&redirect_uri="+REDIRECT_URI);
        String result = HttpUtils.sendGet(ACCESS_TOKEN_URL, stringBuilder.toString());
        String accessToken = StringUtil.substringBetween(result,"access_token=","&");
        return accessToken;
    }

    @Override
    public String getUserId(String accessToken) {
        String result = HttpUtils.sendGet(OPEN_ID_URL, "access_token="+accessToken);
        String userId = StringUtil.substringBetween(result,"\"openid\":\"","\"} )");
        return userId;
    }
}
