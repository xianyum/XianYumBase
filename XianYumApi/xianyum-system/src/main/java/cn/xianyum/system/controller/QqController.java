package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.ThirdOauthRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("xym-system/v1/qq")
@Tag(name = "qq接口")
public class QqController {

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "QQ第三方登录")
    @Permission(publicApi = true)
    public Results<LoginTokenResponse> login(@RequestBody ThirdOauthRequest request) {
        LoginUser loginUser = userService.getUserByQq(request.getAuthCode());
        return Results.success(userTokenService.createToken(loginUser));
    }

    @PostMapping("/bindUser")
    @Operation(summary = "QQ绑定用户")
    public Results<?> bindUser(@RequestBody ThirdOauthRequest aliRequest) {
        boolean isSuccess = userService.bindQqUser(aliRequest.getAuthCode());
        return isSuccess ? Results.success() : Results.error("绑定用户失败！");
    }
}
