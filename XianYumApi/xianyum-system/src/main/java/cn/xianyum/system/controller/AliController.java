package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
