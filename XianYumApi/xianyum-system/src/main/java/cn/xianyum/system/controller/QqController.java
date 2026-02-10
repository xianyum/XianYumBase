package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.UserLoginRequest;
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
    public Results<LoginTokenResponse> login(@RequestBody UserLoginRequest request) {
        request.setLoginType(LoginTypeEnum.QQ);
        return Results.success(userTokenService.login(request));
    }

    @PostMapping("/bindUser")
    @Operation(summary = "QQ绑定用户")
    public Results<?> bindUser(@RequestBody UserLoginRequest userLoginRequest) {
        boolean isSuccess = userService.bindQqUser(userLoginRequest.getAuthCode());
        return isSuccess ? Results.success() : Results.error("绑定用户失败！");
    }
}
