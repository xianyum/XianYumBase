package cn.xianyum.zeekr.service.impl;

import cn.xianyum.common.utils.*;
import cn.xianyum.zeekr.entity.po.ZeekrInterfaceEntity;
import cn.xianyum.zeekr.entity.po.ZeekrPersonEntity;
import cn.xianyum.zeekr.entity.po.ZeekrPersonResult;
import cn.xianyum.zeekr.service.ZeekrPersonInterfaceService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangwei
 * @date 2022/8/7 14:23
 */
@Service
@Slf4j
public class ZeekrPersonInterfaceServiceImpl implements ZeekrPersonInterfaceService {

    @Autowired
    private RedisUtils redisUtils;

    private static final String TOKEN_REDIS_KEY = "XianYumApi:xianyum-zeekr:token:";

    @Override
    public String getToken(String loginName) {
        String redisKey = TOKEN_REDIS_KEY+loginName;
        if (redisUtils.hasKey(redisKey)) {
            return redisUtils.getString(redisKey);
        }
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getTokenUrl();
        String grant_type = "password";
        String client_id = zeekrInterfaceConfig.getClientId();
        String client_secret = zeekrInterfaceConfig.getClientSecret();
        String source_type = "web";
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addBodyPara("grant_type", grant_type)
                .addBodyPara("client_id", client_id)
                .addBodyPara("client_secret", client_secret)
                .addBodyPara("source_type", source_type)
                .addBodyPara("loginName", loginName).post();
        String result = httpResult.getBody().toString();
        JSONObject resultObject = JSONObject.parseObject(result);
        String token = "bearer "+resultObject.getString("access_token");
        if(StringUtil.isNotEmpty(token)){
            Long expires_in = resultObject.getLong("expires_in");
            redisUtils.set(redisKey,token,expires_in);
        }
        return token;
    }

    @Override
    public ZeekrPersonResult getZeekrByDay(String token) {
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getQueryZeekrUrl();
        Date date = new Date();
        String month = DateUtils.format(date, DateUtils.YYYY_MM);
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addHeader("authorization", token)
                .addUrlPara("month", month).get();
        String result = httpResult.getBody().toString();
        List<ZeekrPersonResult> zeekrPersonResults = JSONObject.parseObject(result, new TypeReference<>(){});
        if(zeekrPersonResults == null || zeekrPersonResults.size() == 0){
            return null;
        }
        String nowDay = DateUtils.format(date, DateUtils.DATE_PATTERN);
        Optional<ZeekrPersonResult> first = zeekrPersonResults.stream().filter(p -> p.getDateTime().equals(nowDay)).findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    @Override
    public String executeNowDayZeekr(String token,ZeekrPersonResult zeekrPersonResult){
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getExecuteZeekrUrl();
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addHeader("authorization", token)
                .bodyType(OkHttps.JSON).setBodyPara(zeekrPersonResult).post();
        String result = httpResult.getBody().toString();
        String resultMessage = JSONObject.parseObject(result).getString("message");
        return resultMessage;
    }



    @Override
    public void setEmployeeInfo(ZeekrPersonEntity zeekrPersonEntity) {
        String token = this.getToken(zeekrPersonEntity.getLoginName());
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getEmployeeUrl();
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addHeader("authorization", token).get();
        String result = httpResult.getBody().toString();
        JSONObject resultObject = JSONObject.parseObject(result);
        zeekrPersonEntity.setEmployeeName(resultObject.getString("employeeName"));
        zeekrPersonEntity.setEmployeeNum(resultObject.getString("employeeNum"));
    }

    @Override
    public ZeekrInterfaceEntity getZeekrInterfaceConfig() {
        String zeekr = SystemConstantUtils.getValueByKey("zeekr");
        return JSONObject.parseObject(zeekr,ZeekrInterfaceEntity.class);
    }

    @Override
    public JSONObject getZeekrProjectList(String loginName) {
        if(StringUtil.isEmpty(loginName)){
            return null;
        }
        String token = this.getToken(loginName);
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getQueryZeekrProjectUrl();
        JSONObject requestObject = new JSONObject();
        requestObject.put("size",zeekrInterfaceConfig.getPageSize());
        requestObject.put("page","0");
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addHeader("authorization", token)
                .bodyType(OkHttps.JSON).setBodyPara(requestObject).post();
        String result = httpResult.getBody().toString();
        return JSONObject.parseObject(result);
    }

    @Override
    public List<ZeekrPersonResult> getZeekrByMonth(String loginName) {
        if(StringUtil.isEmpty(loginName)){
            return null;
        }
        String token = this.getToken(loginName);
        ZeekrInterfaceEntity zeekrInterfaceConfig = this.getZeekrInterfaceConfig();
        String url = zeekrInterfaceConfig.getQueryZeekrUrl();
        Date date = new Date();
        String month = DateUtils.format(date, DateUtils.YYYY_MM);
        HttpResult httpResult = HttpUtils.getHttpInstance().sync(url).addHeader("authorization", token)
                .addUrlPara("month", month).get();
        String result = httpResult.getBody().toString();
        List<ZeekrPersonResult> zeekrPersonResults = JSONObject.parseObject(result, new TypeReference<>(){});
        return zeekrPersonResults;
    }
}
