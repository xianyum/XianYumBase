package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.LogService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSON;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private com.anji.captcha.service.CaptchaService captchaService;

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
        long beginTime = System.currentTimeMillis();

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(userRequest.getCaptchaVerification());
        ResponseModel response = captchaService.verification(captchaVO);
        if(response.isSuccess() == false){
            long time = System.currentTimeMillis() - beginTime;
            saveLoginLog(userRequest,"验证码不正确",time);
            return Results.error("验证码不正确");
        }
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication = null;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));

        }catch (Exception e){
            long time = System.currentTimeMillis() - beginTime;
            if (e instanceof BadCredentialsException) {
                saveLoginLog(userRequest,"用户不存在或密码错误",time);
                return Results.error("用户不存在或密码错误");
            }
            else {
                saveLoginLog(userRequest,e.getMessage(),time);
                return Results.error(e.getMessage());
            }
        }

        long time = System.currentTimeMillis() - beginTime;
        saveLoginLog(userRequest,"登录成功",time);

        //生成token，并保存到数据库
        LoginUser loginUserEntity = (LoginUser)authentication.getPrincipal();
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


    /**
     * 获取登录/注册信息
     */
    public void saveLoginLog(UserRequest userRequest,String desc,Long time){
        LogEntity log = new LogEntity();
        log.setMethod("login");
        userRequest.setPassword("");
        log.setParams(JSON.toJSONString(userRequest));
        log.setUsername(userRequest.getUsername());
        log.setOperation(desc);
        log.setTime(time);
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        String ip = IPUtils.getIpAddr(httpServletRequest);
        log.setIp(ip);
        log.setIpInfo(IPUtils.getIpInfo(ip));
        xianYumTaskExecutor.execute(()-> SpringUtils.getBean(LogService.class).saveLog(log));
    }
}
