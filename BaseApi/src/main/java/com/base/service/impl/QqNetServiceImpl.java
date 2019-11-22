package com.base.service.impl;

import com.base.common.utils.HttpUtils;
import com.base.common.utils.StringUtil;
import com.base.service.iservice.QqNetService;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2019/11/22 16:58
 */
@Service
public class QqNetServiceImpl implements QqNetService {

    private static final String ACCESS_TOKEN_URL="https://graph.qq.com/oauth2.0/token";
    private static final String CLIENT_ID="101831000";
    private static final String CLIENT_SECRET="5d8b22dd879c4e942d691d0b7b1940e9";
    private static final String GRANT_TYPE="authorization_code";

    /**
     * 获取QQ用户token
     *
     * @param authCode
     * @return
     */
    @Override
    public String getAccessToken(String authCode,String redirectUri) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("grant_type="+GRANT_TYPE);
        stringBuilder.append("&client_id="+CLIENT_SECRET);
        stringBuilder.append("&client_secret="+GRANT_TYPE);
        stringBuilder.append("&code="+authCode);
        stringBuilder.append("&redirect_uri="+redirectUri);
        String result = HttpUtils.sendGet(ACCESS_TOKEN_URL, stringBuilder.toString());
        String accessToken = StringUtil.substringBetween(result,"access_token=","&");
        return accessToken;
    }
}
