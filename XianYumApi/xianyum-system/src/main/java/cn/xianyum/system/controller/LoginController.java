package cn.xianyum.system.controller;

import cn.xianyum.common.async.AsyncManager;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.validator.ValidatorUtils;
import cn.xianyum.system.common.factory.AsyncLogFactory;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.po.LoginUserEntity;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.CaptchaService;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson.JSON;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private com.anji.captcha.service.CaptchaService captchaService1;

    @Resource
    private AuthenticationManager authenticationManager;

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

    /**
     * 登录
     * @param userRequest
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录系统", httpMethod = "POST")
    public DataResult login(@RequestBody UserRequest userRequest) {
        long beginTime = System.currentTimeMillis();

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(userRequest.getCaptchaVerification());
        ResponseModel response = captchaService1.verification(captchaVO);
        if(response.isSuccess() == false){
            long time = System.currentTimeMillis() - beginTime;
            saveLoginLog(userRequest,"验证码不正确",time);
            return DataResult.error("验证码不正确");
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
                return DataResult.error("用户不存在或密码错误");
            }
            else {
                saveLoginLog(userRequest,e.getMessage(),time);
                return DataResult.error(e.getMessage());
            }
        }

        long time = System.currentTimeMillis() - beginTime;
        saveLoginLog(userRequest,"登录成功",time);

        //生成token，并保存到数据库
        LoginUserEntity loginUserEntity = (LoginUserEntity)authentication.getPrincipal();
        UserEntity userEntity = BeanUtils.copy(loginUserEntity, UserEntity.class);
        DataResult result = userTokenService.createToken(userEntity);
        return result;
    }


    /**
     * 获取手机验证码
     */
    @PostMapping("/getPhoneCode")
    @ApiOperation(value = "获取手机验证码", httpMethod = "POST")
    public DataResult getPhoneCode(@RequestBody UserRequest request) {
        //captchaService.getPhoneCaptcha(request);
        return DataResult.error("短信接口停用，无法发送短信！");
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册用户", httpMethod = "POST")
    public DataResult register(@RequestBody UserRequest request) {
        ValidatorUtils.validateEntity(request);
        long beginTime = System.currentTimeMillis();
        boolean captcha = captchaService.validate(request.getUuid(), request.getCaptcha());
        if(!captcha){
            return DataResult.error("验证码不正确");
        }
        Integer count = userService.save(request);
        if(count >0){
            long time = System.currentTimeMillis() - beginTime;
            saveLoginLog(request,"注册成功",time);
            return DataResult.success();
        }
        long time = System.currentTimeMillis() - beginTime;
        saveLoginLog(request,"注册失败",time);
        return DataResult.error("注册失败");
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
        AsyncManager.async().execute(AsyncLogFactory.save(log));
    }
}
