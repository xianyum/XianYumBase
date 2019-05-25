package com.base.controller;


import com.base.common.exception.SoException;
import com.base.common.utils.DataResult;
import com.base.common.validator.ValidatorUtils;
import com.base.common.validator.group.AddGroup;
import com.base.common.validator.group.UpdateGroup;
import com.base.entity.enums.UserStatusEnum;
import com.base.entity.po.UserEntity;
import com.base.entity.request.LoginPhoneRequest;
import com.base.entity.request.RegisterInfoRequest;
import com.base.entity.request.UserRequest;
import com.base.service.iservice.CaptchaService;
import com.base.service.iservice.RegisterService;
import com.base.service.iservice.UserService;
import com.base.service.iservice.UserTokenService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/***
 * 登录相关
 */
@RestController
public class LoginController {
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RegisterService registerService;

    /**
     * 验证码
     * 设置produces = MediaType.IMAGE_JPEG_VALUE（）import org.springframework.http.MediaType;
     * 可以在swagger打开，但是不输入uuid返回不了错误
     */
    @GetMapping(value = "captcha.jpg" ,produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "获取验证码", httpMethod = "GET", notes = "获取验证码")
    public void captcha(HttpServletResponse response, String uuid)throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = captchaService.getCaptcha(uuid);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录系统", httpMethod = "POST", notes = "登录接口")
    public DataResult login(@RequestBody UserRequest userRequest) {
        try {
            boolean captcha = captchaService.validate(userRequest.getUuid(), userRequest.getCaptcha());
            if(!captcha){
                return DataResult.error("验证码不正确");
            }
            //用户信息
            UserEntity user = userService.queryByUserName(userRequest.getUsername());
            //账号不存在、密码错误
            if(user == null || !user.getPassword().equals(new Sha256Hash(userRequest.getPassword(), user.getSalt()).toHex())) {
                return DataResult.error("账号或密码不正确");
            }
            //账号锁定
            if(user.getStatus() == UserStatusEnum.BAN.getStatus()){
                return DataResult.error("账号已被锁定,请联系管理员");
            }

            //生成token，并保存到数据库
            DataResult result = userTokenService.createToken(user);
            return result;
        }catch (SoException e){
            throw new SoException("系统异常");
        }
    }
    /**
     * 退出
     */
    @PostMapping("/logout")
    @ApiOperation(value = "注销系统", httpMethod = "POST", notes = "注销接口")
    public DataResult logout() {
        userTokenService.logout();
        return DataResult.success();
    }

    @PostMapping("/getRegisterCode")
    @ApiOperation(value = "发送邮件获取注册验证码", httpMethod = "POST", notes = "发送邮件获取注册验证码")
    public DataResult getRegisterCode(@RequestBody RegisterInfoRequest request) {
        ValidatorUtils.validateEntity(request, AddGroup.class);
        try {
            captchaService.createRegisterCode(request);
            return DataResult.success();
        }catch (SoException e){
            throw new SoException("发送邮件失败");
        }
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册", httpMethod = "POST", notes = "注册")
    public DataResult register(@RequestBody RegisterInfoRequest request) {
        ValidatorUtils.validateEntity(request, UpdateGroup.class);
        boolean captcha = captchaService.registerValidate(request.getUuid(), request.getCode());
        if(!captcha){
            return DataResult.error("验证码不正确");
        }
        registerService.insert(request);
        return DataResult.success();
    }

    @PostMapping("/getPhoneCode")
    @ApiOperation(value = "手机登录发送验证码", httpMethod = "POST", notes = "手机登录发送验证码")
    public DataResult getPhoneCode(@RequestBody LoginPhoneRequest request) {
        captchaService.createPhoneCode(request);
        return DataResult.success();
    }

    @PostMapping("/loginPhone")
    @ApiOperation(value = "手机登录系统", httpMethod = "POST", notes = "手机登录接口")
    public DataResult loginPhone(@RequestBody LoginPhoneRequest request) {
        boolean captcha = captchaService.registerValidate(request.getUuid(), request.getCode());
        if(!captcha){
            return DataResult.error("验证码不正确");
        }
        //用户信息
        UserEntity user = userService.queryByPhone(request.getPhone());
        //账号不存在、密码错误
        if(user == null) {
            return DataResult.error("手机号不正确");
        }
        //账号锁定
        if(user.getStatus() == UserStatusEnum.BAN.getStatus()){
            return DataResult.error("账号已被锁定,请联系管理员");
        }
        //生成token，并保存到数据库
        DataResult result = userTokenService.createToken(user);
        return result;
    }
}
