package com.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.common.annotation.SysLog;
import com.base.common.utils.DataResult;
import com.base.entity.po.UserEntity;
import com.base.service.iservice.UserService;
import com.base.service.iservice.UserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.buf.UEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

/**
 * @author zhangwei
 * @date 2019/11/9 17:24
 * @desc
 */
@RestController
@RequestMapping("/ali")
@Api(tags = "支付宝接口")
public class AliController {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @SysLog(value = "支付宝第三方登录")
    @ApiOperation(value = "支付宝第三方登录", httpMethod = "POST")
    public DataResult login(@RequestBody String requestInfo) {
        JSONObject jsonObject = JSON.parseObject(requestInfo);
        String authCode = jsonObject.getString("authCode");
        UserEntity user = userService.getUserByAli(authCode);
        if(user != null){
            return userTokenService.createToken(user);
        }else {
            return DataResult.error();
        }
    }
}
