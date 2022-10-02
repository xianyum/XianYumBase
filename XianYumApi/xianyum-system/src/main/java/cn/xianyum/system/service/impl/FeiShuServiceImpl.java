package cn.xianyum.system.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.service.FeiShuService;
import cn.xianyum.system.service.SystemConstantService;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2022/4/18 21:39
 */
@Slf4j
@Service
public class FeiShuServiceImpl implements FeiShuService {

    @Autowired
    private SystemConstantService systemConstantService;

    @Override
    public String callback(String code, String key) {
        SystemConstantEntity feiShuInfo = systemConstantService.getByKey(key);
        if(feiShuInfo == null){
            throw new SoException("暂未飞书配置信息");
        }

        JSONObject feiShuObj = JSONObject.parseObject(feiShuInfo.getConstantValue());

        String appId = feiShuObj.getString("appId");
        String appsecret = feiShuObj.getString("appSecret");
        String tokenUrl = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";

        JSONObject tokenObject = new JSONObject();
        tokenObject.put("app_id",appId);
        tokenObject.put("app_secret",appsecret);
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(tokenUrl)
                .bodyType(OkHttps.JSON).setBodyPara(tokenObject).post();
        String tokenResult = httpResult.getBody().toString();
        JSONObject jsonObject = JSONObject.parseObject(tokenResult);
        String token = "Bearer "+jsonObject.getString("tenant_access_token");

        String dataUrl = "https://open.feishu.cn/open-apis/authen/v1/access_token";
        JSONObject dataObject = new JSONObject();
        dataObject.put("grant_type","authorization_code");
        dataObject.put("code",code);
        HttpResult httpResult1 = HttpUtils.getHttpInstance().sync(dataUrl)
                .addHeader("Authorization",token).bodyType(OkHttps.JSON).setBodyPara(dataObject).post();
        return httpResult1.getBody().toString();
    }
}
