package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.UserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 登录相关
 * @author zhangwei
 * @date 2019/5/25 20:12
 * @email 80616059@qq.com
 */
@RestController
@Api(tags = "登录接口")
@Slf4j
public class LoginController {

    @Autowired
    private UserTokenService userTokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 登录
     * @param userRequest
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录系统")
    public Results login(@RequestBody UserRequest userRequest) {
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
     * 获取手机验证码
     */
    @PostMapping("/getPhoneCode")
    @ApiOperation(value = "获取手机验证码")
    @Permission(publicApi = true)
    public Results getPhoneCode(@RequestBody UserRequest request) {
        //captchaService.getPhoneCaptcha(request);
        return Results.error("短信接口停用，无法发送短信！");
    }


    @PostMapping("/xianYuLogin")
    @ApiOperation(value = "咸鱼客户端登录系统")
    @Permission(publicApi = true)
    public Results xianYuLogin(@RequestBody UserRequest userRequest) {
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
