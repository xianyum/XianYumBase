package cn.xianyum.system.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.request.CheckCaptchaRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.UserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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

    /**
     * 登录
     * @param userRequest
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录系统")
    @Permission(publicApi = true)
    public Results<?> login(@RequestBody UserRequest userRequest) {
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
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
        loginUserEntity.setLoginType(LoginTypeEnum.SYSTEM.getAccountType());
        Results result = userTokenService.createToken(loginUserEntity);
        return result;
    }


    /**
     * 生成验证码
     * @return 验证码数据
     */
    @PostMapping("/genCaptcha")
    @Permission(publicApi = true)
    @Operation(summary = "获取验证码")
    public ApiResponse<ImageCaptchaVO> genCaptcha() {
        // 1.生成验证码(该数据返回给前端用于展示验证码数据)
        // 参数1为具体的验证码类型， 默认支持 SLIDER、ROTATE、WORD_IMAGE_CLICK、CONCAT 等验证码类型，详见： `CaptchaTypeConstant`类
        return  imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    /**
     * 校验验证码
     * @param request 验证码数据
     * @return 校验结果
     */
    @PostMapping("/check")
    @ResponseBody
    @Operation(summary = "校验验证码")
    public ApiResponse<?> checkCaptcha(@RequestBody CheckCaptchaRequest request) {
        ApiResponse<?> response = imageCaptchaApplication.matching(request.getId(), request.getImageCaptchaTrack());
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", request.getId()));
        }
        return response;
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
    public Results<?> xianYuLogin(@RequestBody UserRequest userRequest) {
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
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
