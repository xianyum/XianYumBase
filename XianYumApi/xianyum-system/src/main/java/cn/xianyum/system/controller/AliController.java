package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.service.AliNetService;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
@RequestMapping("xianyum-system/v1/ali")
@Api(tags = "阿里相关接口")
public class AliController {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private AliNetService aliNetService;

    @PostMapping("/login")
    @SysLog(value = "支付宝第三方登录")
    @ApiOperation(value = "支付宝第三方登录")
    @Permission(publicApi = true)
    public Results login(@RequestBody String requestInfo) {
        JSONObject jsonObject = JSON.parseObject(requestInfo);
        String authCode = jsonObject.getString("authCode");
        LoginUser loginUser = userService.getUserByAli(authCode);
        if(loginUser != null){
            return userTokenService.createToken(loginUser);
        }else {
            return Results.error();
        }
    }

    @PostMapping("/yunXiao/flowCallBack")
    @SysLog(value = "阿里云-云效-流水线执行结果回调")
    @ApiOperation(value = "阿里云-云效-流水线执行结果回调")
    @Permission(publicApi = true)
    public Results flowCallBack(@RequestBody String requestInfo) {
        aliNetService.yunXiaoFlowCallBack(requestInfo);
        return Results.success();
    }

}
