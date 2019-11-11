package com.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.base.common.utils.DataResult;
import com.base.common.utils.HttpUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

/**
 * @author zhangwei
 * @date 2019/11/8 13:27
 */
@RestController
@RequestMapping("/third")
@Api(tags = "第三方登录接口")
@Slf4j
public class ThirdLoginController {

    @GetMapping("/gitee/login")
    public DataResult gitee(@RequestParam("code") String code){
        //https://gitee.com/oauth/token?
        // grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
        String clientId = "2d69545ece41481a2162da4dd4863dce1739830076791991a9c786eeba754e71";
        String redirectUri = "http://47.103.20.104:9160/third/gitee/login";
        String clientSecret = "ddcc46a1a5a9e0c6c219c7e0830670986a22bb7e42f9458eedb2c631f78087af";
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("grant_type","authorization_code");
        jsonObject.put("code",code);
        jsonObject.put("client_id",clientId);
        jsonObject.put("redirect_uri",redirectUri);
        jsonObject.put("client_secret",clientSecret);
        String result = HttpUtils.sendPostJson("https://gitee.com/oauth/token", jsonObject.toString());
        log.info(result);
        return DataResult.success();
    }

    public static void main(String[] args) throws Exception{
        String url = "http://xianyum.cn:9299";
        String encode = URLEncoder.encode(url, "utf-8");
        System.out.println(encode);
    }
}
