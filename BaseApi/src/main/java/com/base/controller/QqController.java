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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2019/11/23 23:20
 * @desc
 */
@RestController
@RequestMapping("/qq")
@Api(tags = "qq接口")
public class QqController {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @SysLog(value = "QQ第三方登录")
    @ApiOperation(value = "QQ第三方登录", httpMethod = "POST")
    public DataResult login(@RequestBody String requestInfo) {
        JSONObject jsonObject = JSON.parseObject(requestInfo);
        String authCode = jsonObject.getString("authCode");
        UserEntity user = userService.getUserByQq(authCode);
        if(user != null){
            return userTokenService.createToken(user);
        }else {
            return DataResult.error();
        }
    }

}
