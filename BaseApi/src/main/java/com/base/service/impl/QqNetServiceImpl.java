package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.base.common.utils.HttpUtils;
import com.base.common.utils.StringUtil;
import com.base.entity.po.QqUserEntity;
import com.base.service.iservice.QqNetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${qq.login.access_token_url}")
    private String ACCESS_TOKEN_URL;

    @Value("${qq.login.client_id}")
    private String CLIENT_ID;

    @Value("${qq.login.client_secret}")
    private String CLIENT_SECRET;

    @Value("${qq.login.grant_type}")
    private String GRANT_TYPE;

    @Value("${qq.login.redirect_uri}")
    private String REDIRECT_URI;

    /** 获取OpenID */
    @Value("${qq.login.open_id_url}")
    private String OPEN_ID_URL;

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
    public QqUserEntity getUserId(String accessToken) {
        String result = HttpUtils.sendGet(OPEN_ID_URL, "access_token="+accessToken);
        log.info("第三方QQ登录,{}",result);
        String userId = StringUtil.substringBetween(result,"\"openid\":\"","\"} )");
        StringBuilder sb = new StringBuilder();
        sb.append("access_token="+accessToken);
        sb.append("&oauth_consumer_key="+CLIENT_ID);
        sb.append("&openid="+userId);
        sb.append("&format=json");
        String userJson = HttpUtils.sendGet("https://graph.qq.com/user/get_user_info", sb.toString());
        log.info("获取QQ用户信息,{}",userJson);
        QqUserEntity qqUserEntity = JSONObject.parseObject(userJson,QqUserEntity.class);
        if(qqUserEntity == null){
            qqUserEntity = new QqUserEntity();
        }
        qqUserEntity.setUserId(userId);
        return qqUserEntity;
    }

}
