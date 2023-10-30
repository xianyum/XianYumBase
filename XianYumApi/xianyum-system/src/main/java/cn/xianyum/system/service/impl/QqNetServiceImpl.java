package cn.xianyum.system.service.impl;

import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.entity.po.QqUserEntity;
import cn.xianyum.system.service.QqNetService;
import com.alibaba.fastjson2.JSONObject;
import com.ejlchina.okhttps.HttpResult;
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

    @Value("${qq.login.user_info_url}")
    private String USER_INFO_URL;

    /**
     * 获取QQ用户token
     *
     * @param authCode
     * @return
     */
    @Override
    public String getAccessToken(String authCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?grant_type="+GRANT_TYPE);
        stringBuilder.append("&client_id="+CLIENT_ID);
        stringBuilder.append("&client_secret="+CLIENT_SECRET);
        stringBuilder.append("&code="+authCode);
        stringBuilder.append("&redirect_uri="+REDIRECT_URI);
        HttpResult result = HttpUtils.getHttpInstance().sync(ACCESS_TOKEN_URL + stringBuilder.toString()).get();
        String accessToken = StringUtil.substringBetween(result.getBody().toString(),"access_token=","&");
        return accessToken;
    }

    @Override
    public QqUserEntity getUserId(String accessToken) {
        HttpResult result = HttpUtils.getHttpInstance().sync(OPEN_ID_URL + "?access_token="+accessToken).get();
        String resultResponse = result.getBody().toString();
        log.info("第三方QQ登录,{}",resultResponse);
        String userId = StringUtil.substringBetween(resultResponse,"\"openid\":\"","\"} )");
        StringBuilder sb = new StringBuilder();
        sb.append("?access_token="+accessToken);
        sb.append("&oauth_consumer_key="+CLIENT_ID);
        sb.append("&openid="+userId);
        sb.append("&format=json");
        HttpResult userJson = HttpUtils.getHttpInstance().sync(USER_INFO_URL + sb.toString()).get();
        String userJsonResponse = userJson.getBody().toString();
        log.info("获取QQ用户信息,{}",userJsonResponse);
        QqUserEntity qqUserEntity = JSONObject.parseObject(userJsonResponse,QqUserEntity.class);
        if(qqUserEntity == null){
            qqUserEntity = new QqUserEntity();
        }
        qqUserEntity.setUserId(userId);
        return qqUserEntity;
    }

}
