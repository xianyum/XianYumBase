package cn.xianyum.system.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.CaptchaConstant;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.request.CheckCaptchaRequest;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.service.UserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

/**
 * 登录相关
 * @author zhangwei
 * @date 2019/5/25 20:12
 * @email 80616059@qq.com
 */
@RestController
@Tag(name = "登录接口")
@Slf4j
public class LoginController {

    @Resource
    private UserTokenService userTokenService;

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @PostMapping("/login")
    @Operation(summary = "登录系统")
    @Permission(publicApi = true)
    public Results<LoginTokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return Results.success(this.userTokenService.login(request));
    }

    /**
     * 生成验证码
     * @return 验证码数据
     */
    @PostMapping("/captcha/genCaptcha")
    @Permission(publicApi = true)
    @Operation(summary = "获取验证码")
    public ApiResponse<ImageCaptchaVO> genCaptcha() {
        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(SystemConstantUtils.getValueByKey(SystemConstantKeyEnum.CAPTCHA_TYPE));
        generateParam.addParam(ParamKeyEnum.FONT_TAG, CaptchaConstant.FONT_TAG);
        return imageCaptchaApplication.generateCaptcha(generateParam);
    }

    /**
     * 校验验证码
     * @param request 验证码数据
     * @return 校验结果
     */
    @PostMapping("/captcha/check")
    @Operation(summary = "校验验证码")
    @Permission(publicApi = true)
    public ApiResponse<?> checkCaptcha(@RequestBody CheckCaptchaRequest request) {
        ApiResponse<?> response = imageCaptchaApplication.matching(request.getId(), request.getData());
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", request.getId()));
        }
        return response;
    }


    /**
     * 生成验证码
     * @return 验证码数据
     */
    @GetMapping("/captcha/getType")
    @Permission(publicApi = true)
    @Operation(summary = "获取验证码类型")
    public Results<String> getCaptchaType() {
       return Results.success(SystemConstantUtils.getValueByKey(SystemConstantKeyEnum.CAPTCHA_TYPE));
    }

    /**
     * 获取手机验证码
     */
    @PostMapping("/sendLoginCredentials")
    @Operation(summary = "发送登录凭证")
    @Permission(publicApi = true)
    public Results<?> sendLoginCredentials(@RequestBody UserLoginRequest request) {
        this.userTokenService.sendLoginCredentials(request);
        return Results.success();
    }

    @PostMapping("/xianYuLogin")
    @Operation(summary = "咸鱼客户端登录系统")
    @Permission(publicApi = true)
    public Results<?> xianYuLogin(@RequestBody UserLoginRequest request) {
        // todo 后期得改造xianyu PC客户端登录方式
        return Results.success();
    }
}
