package cn.xianyum.system.service.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.service.MenuService;
import cn.xianyum.system.service.RoleService;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSONObject;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.util.Date;
import java.util.Objects;


@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Value("${redis.token.expire:30}")
    private Integer expire;

    @Value("${redis.token.prefix:token}")
    private String prefix;

    @Value("${redis.token.credentials_prefix:credentials_prefix}")
    private String credentialsPrefix;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Resource
    private MessageSender messageSender;

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 获取当前登录user
     * @return
     */
    @Override
    public LoginUser getUserSelf(){
        return SecurityUtils.getLoginUser();
    }

    @Override
    public void setExtraUserInfo(LoginUser loginUser) {
        // 增加浏览器标识以及登录ip等
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
        String ip = IPUtils.getIpAddr(httpServletRequest);
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(IPUtils.getIpInfo(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
        loginUser.setLoginTime(new Date());

        // =======设置权限===========
        this.roleService.setLoginUserRoleService(loginUser);
        // 设置菜单权限
        loginUser.setPermissions(menuService.getMenuPermission(loginUser.getId()));
    }

    /**
     * 通过httpRequest获取当前登录用户
     *
     * @return
     */
    @Override
    public LoginUser getLoginUserByHttpRequest() {
        String token = HttpContextUtils.getRequestToken();
        String userEntityJson = (String)SpringUtils.getBean(RedisUtils.class).get(prefix+token);
        LoginUser loginUser = JSONObject.parseObject(userEntityJson, LoginUser.class);
        return loginUser;
    }

    /**
     * 发送验证码
     *
     * @param request
     */
    @Override
    public void sendLoginCredentials(UserLoginRequest request) {
        if(StringUtil.isBlank(request.getUsername())
                || Objects.isNull(request.getLoginType()) || StringUtil.isBlank(request.getVerifyCode())){
            throw new SoException("发送失败");
        }
        // 校验滑动验证码
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
        if (!valid) {
            throw new SoException("验证码错误");
        }
        String code = UUIDUtils.getRandomNumber(6);
        switch (request.getLoginType()){
            case EMAIL -> this.sendLoginCredentialsByEmail(request,code);
            default -> throw new SoException("目前只支持发送邮箱验证码");
        }
        // 缓存redis,设置为1分钟
        redisUtils.setMin(String.format(credentialsPrefix,request.getUsername()),code,3);
    }

    /**
     * 发送邮箱验证码
     *
     * @param request
     * @param code
     */
    @Override
    public void sendLoginCredentialsByEmail(UserLoginRequest request, String code) {
        Context context = new Context();
        context.setVariable("emailCode", code);
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setTitle("XianYum验证码通知");
        messageSenderEntity.setEmailHtmlPath("codeMail");
        messageSenderEntity.setEmailToUser(request.getUsername());
        messageSender.sendAsyncEmailTemplateMessage(MessageCodeEnums.XIANYU_CONFIG_DETAIL_NOTIFY.getMessageCode(),messageSenderEntity,context);
    }

    /**
     * 登录核心
     *
     * @param request
     * @return
     */
    @Override
    public LoginTokenResponse login(UserLoginRequest request) {
        LoginUser loginUserEntity = switch(request.getLoginType()){
            case XIAN_YU, USER_PASSWORD -> this.loginPwd(request);
            case EMAIL -> this.loginEmail(request);
            case QQ -> this.loginByQq(request);
            case ZHI_FU_BAO -> this.loginByZhiFuBao(request);
        };
        return this.createToken(loginUserEntity);
    }

    /**
     * 账号密码登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginUser loginPwd(UserLoginRequest request) {
        Authentication authentication;
        try {
            boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
            if (!valid && request.getLoginType().equals(LoginTypeEnum.USER_PASSWORD)) {
                throw new SoException("验证码错误");
            }
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            if (e instanceof BadCredentialsException) {
                throw new SoException("密码错误");
            } else {
                throw new SoException(e.getMessage());
            }
        }
        LoginUser loginUserEntity = (LoginUser)authentication.getPrincipal();
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }

    /**
     * 邮箱验证码登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginUser loginEmail(UserLoginRequest request) {
        String redisKey = String.format(credentialsPrefix,request.getUsername());
        if(!redisUtils.hasKey(redisKey)){
            throw new SoException("验证码已过期");
        }
        if(StringUtil.isBlank(request.getCode()) || !request.getCode().equals(redisUtils.getString(redisKey))){
            throw new SoException("验证码错误");
        }
        // 校验登录凭证成功就删除
        redisUtils.del(redisKey);
        UserDetails userDetails = SpringUtils.getBean(UserDetailsService.class).loadUserByUsername(request.getUsername());
        // 第三步：构建认证信息（密码置空，跳过密码校验）
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUserEntity = (LoginUser) authentication.getPrincipal();
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }

    /**
     * QQ授权登录
     *
     * @return
     */
    @Override
    public LoginUser loginByQq(UserLoginRequest request) {
        LoginUser user = SpringUtils.getBean(UserService.class).getUserByQq(request);
        UserDetails userDetails =  JSONObject.parseObject(JSONObject.toJSONString(user),LoginUser.class);
        // 第三步：构建认证信息（密码置空，跳过密码校验）
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUserEntity = (LoginUser) authentication.getPrincipal();
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }

    /**
     * 支付宝授权登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginUser loginByZhiFuBao(UserLoginRequest request) {
        LoginUser user = SpringUtils.getBean(UserService.class).getUserByAli(request.getAuthCode());
        UserDetails userDetails =  JSONObject.parseObject(JSONObject.toJSONString(user),LoginUser.class);
        // 第三步：构建认证信息（密码置空，跳过密码校验）
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUserEntity = (LoginUser) authentication.getPrincipal();
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }


    @Override
    public LoginTokenResponse createToken(LoginUser loginUser) {
        //生成token
        String token = UUIDUtils.UUIDReplace();
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 86400L * 1000L);
        // 设置额外用户信息
        this.setExtraUserInfo(loginUser);
        redisUtils.setDay(prefix+token,JSONObject.toJSONString(loginUser),expire);
        LoginTokenResponse loginTokenResponse = new LoginTokenResponse();
        loginTokenResponse.setToken(token);
        loginTokenResponse.setExpireTime(expireTime);
        return loginTokenResponse;
    }

    @Override
    public void logout() {
        String token =  HttpContextUtils.getRequestToken();
        redisUtils.del(prefix+token);
    }

    @Override
    public void refreshUser() {
        String token = HttpContextUtils.getRequestToken();
        UserEntity userEntity = userMapper.selectById(SecurityUtils.getLoginUser().getId());

        LoginUser loginUser = BeanUtils.copy(userEntity,LoginUser.class);
        // 保留本来登录的字段，不能被覆盖。
        loginUser.setLoginType(SecurityUtils.getLoginUser().getLoginType());

        this.setExtraUserInfo(loginUser);
        redisUtils.setDay(prefix+token,JSONObject.toJSONString(loginUser),expire);
    }
}
