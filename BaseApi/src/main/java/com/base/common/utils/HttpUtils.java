package com.base.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://okhttps.ejlchina.com/v2/getstart.html#maven
 * @author zhangwei
 * @date 2020-12-4 19:24:32
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 发送post请求（application/json）
     * @param url 请求url
     * @param requestObject 可以是String-json类型，也可以是Object
     * @return
     */
    public static String sendPost(String url,Object requestObject){

        if(StringUtil.isEmpty(url) || null == requestObject){
            return null;
        }
        try {
            HttpResult httpResult = com.ejlchina.okhttps.HttpUtils.sync(url)
                    .bodyType(OkHttps.JSON).setBodyPara(requestObject).post();
            if(httpResult.isSuccessful()){
                return httpResult.getBody().toString();
            }
        }catch (Exception e){
            log.error("send post json request error. {},{},{}",url,JSONObject.toJSONString(requestObject),e);
        }
        return null;
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {

        if(StringUtil.isEmpty(url)){
            return null;
        }
        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append(url);
        if(StringUtil.isNotEmpty(param)){
            requestUrl.append("?");
            requestUrl.append(param);
        }
        try {
            HttpResult httpResult = com.ejlchina.okhttps.HttpUtils.sync(requestUrl.toString()).get();
            if(httpResult.isSuccessful()){
                return httpResult.getBody().toString();
            }
        }catch (Exception e){
            log.error("send get request error. {},{}",requestUrl.toString(),e);
        }
        return null;
    }

}
