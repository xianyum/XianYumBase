package cn.xianyum.system.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.CaptchaConstant;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.request.CheckCaptchaRequest;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.UserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private AuthenticationManager authenticationManager;

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;


    @PostMapping("/login")
    @Operation(summary = "登录系统")
    @Permission(publicApi = true)
    public Results<?> login(@RequestBody @Valid UserLoginRequest request) {
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication;
        try {
            boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
            if (!valid) {
                return Results.error("验证码错误");
            }
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            if (e instanceof BadCredentialsException) {
                return Results.error("密码错误,请重试");
            } else {
                return Results.error(e.getMessage());
            }
        }
        //生成token，并保存到数据库
        LoginUser loginUserEntity = (LoginUser)authentication.getPrincipal();
        loginUserEntity.setLoginType(LoginTypeEnum.SYSTEM.getAccountType());
        Results result = userTokenService.createToken(loginUserEntity);
        return result;
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
    @PostMapping("/getPhoneCode")
    @Operation(summary = "获取手机验证码")
    @Permission(publicApi = true)
    public Results<?> getPhoneCode(@RequestBody UserRequest request) {
        //captchaService.getPhoneCaptcha(request);
        return Results.error("短信接口停用，无法发送短信！");
    }


    @PostMapping("/xianYuLogin")
    @Operation(summary = "咸鱼客户端登录系统")
    @Permission(publicApi = true)
    public Results<?> xianYuLogin(@RequestBody UserLoginRequest request) {
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            if (e instanceof BadCredentialsException) {
                return Results.error("用户不存在或密码错误");
            }
            else {
                return Results.error(e.getMessage());
            }
        }
        //生成token，并保存到数据库
        LoginUser loginUserEntity = (LoginUser)authentication.getPrincipal();
        loginUserEntity.setLoginType(LoginTypeEnum.XIAN_YU.getAccountType());
        Results result = userTokenService.createToken(loginUserEntity);
        return result;
    }
}
