package cn.xianyum.system.service.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.QrCodeLoginStatus;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.common.utils.SecretUtils;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.entity.response.QrLoginTicketResponse;
import cn.xianyum.system.service.MenuService;
import cn.xianyum.system.service.RoleService;
import cn.xianyum.system.service.UserService;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSONObject;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.util.Date;
import java.util.Objects;


@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Value("${redis.token.expire:30}")
    private Integer expire;

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

    }


    /**
     * 发送验证码
     *
     * @param request
     */
    @Override
    public void sendLoginCredentials(UserLoginRequest request) {
        if(StrUtil.isBlank(request.getUsername())
                || Objects.isNull(request.getLoginType()) || StrUtil.isBlank(request.getVerifyCode())){
            throw new SoException("发送失败");
        }
        // 校验滑动验证码
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
        if (!valid) {
            throw new SoException("验证码错误");
        }
        String code = IdGeneratorUtil.getRandomNumber(6);
        switch (request.getLoginType()){
            case EMAIL -> this.sendLoginCredentialsByEmail(request,code);
            default -> throw new SoException("目前只支持发送邮箱验证码");
        }
        // 缓存redis,设置为1分钟
        redisUtils.setMin(String.format(RedisKeyEnum.TOKEN_CREDENTIALS_PREFIX.getKey(),request.getUsername()),code,3);
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
            case QR -> this.loginByQr(request);
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
        if(StrUtil.isBlank(request.getUsername()) || StrUtil.isBlank(request.getPassword())){
            throw new SoException("账号密码不能为空");
        }
        if(StrUtil.isBlank(request.getVerifyCode())){
            throw new SoException("未通过验证码校验");
        }
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
        if (!valid && request.getLoginType().equals(LoginTypeEnum.USER_PASSWORD)) {
            throw new SoException("验证码错误");
        }
        UserEntity userEntity = SpringUtil.getBean(UserService.class).queryByUserName(request.getUsername());
        if(Objects.isNull(userEntity)){
            throw new SoException("用户不存在");
        }
        if(userEntity.getStatus() == YesOrNoEnum.NO.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }
        if(!SecretUtils.matchesPassword(request.getPassword(),userEntity.getPassword())){
            throw new SoException("密码错误");
        }
        LoginUser loginUserEntity = new LoginUser();
        loginUserEntity.setId(userEntity.getId());
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
        if(StrUtil.isBlank(request.getUsername())){
            throw new SoException("邮箱账号不能为空");
        }
        String redisKey = String.format(RedisKeyEnum.TOKEN_CREDENTIALS_PREFIX.getKey(),request.getUsername());
        if(!redisUtils.hasKey(redisKey)){
            throw new SoException("验证码已过期");
        }
        if(StrUtil.isBlank(request.getCode()) || !request.getCode().equals(redisUtils.getString(redisKey))){
            throw new SoException("验证码错误");
        }
        UserEntity userEntity = SpringUtil.getBean(UserService.class).queryByUserName(request.getUsername());
        if(Objects.isNull(userEntity)){
            throw new SoException("用户不存在");
        }
        if(userEntity.getStatus() == YesOrNoEnum.NO.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }
        // 校验登录凭证成功就删除
        redisUtils.del(redisKey);
        LoginUser loginUserEntity = new LoginUser();
        loginUserEntity.setId(userEntity.getId());
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
        LoginUser loginUserEntity = SpringUtil.getBean(UserService.class).getUserByQq(request);
        if(loginUserEntity.getStatus() == YesOrNoEnum.NO.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }
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
        LoginUser loginUserEntity = SpringUtil.getBean(UserService.class).getUserByAli(request.getAuthCode());
        if(loginUserEntity.getStatus() == YesOrNoEnum.NO.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }

    /**
     * 生成二维码登录凭证
     *
     * @return
     */
    @Override
    public QrLoginTicketResponse generateQrCode() {
        QrLoginTicketResponse qrLoginTicketResponse = new QrLoginTicketResponse();
        String qrTicket = IdUtil.getSnowflakeNextIdStr();
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.UNSCANNED);
        qrLoginTicketResponse.setTicket(qrTicket);
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), qrTicket);
        redisUtils.setMin(redisKey,JSONObject.toJSONString(qrLoginTicketResponse),2);
        return qrLoginTicketResponse;
    }

    /**
     * 扫描二维码
     *
     * @return
     */
    @Override
    public void scanQrCode(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        if(!redisUtils.hasKey(redisKey)){
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if(!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.UNSCANNED)){
            throw new SoException("二维码已被使用，请刷新");
        }
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.SCANNED);
        redisUtils.setMin(redisKey,JSONObject.toJSONString(qrLoginTicketResponse),2);
    }

    /**
     * 二维码确认登录
     *
     * @param ticket
     */
    @Override
    public void confirmQrCode(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        if(!redisUtils.hasKey(redisKey)){
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if(!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.SCANNED)){
            throw new SoException("二维码已被使用，请刷新");
        }
        qrLoginTicketResponse.setUserName(UserContextHolder.getLoginUser().getUsername());
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.CONFIRMED);
        redisUtils.setMin(redisKey,JSONObject.toJSONString(qrLoginTicketResponse),2);
    }

    /**
     * 二维码确认登录
     *
     * @param ticket
     */
    @Override
    public QrLoginTicketResponse getQrCodeStatus(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        QrLoginTicketResponse response = new QrLoginTicketResponse();
        if(!redisUtils.hasKey(redisKey)){
            response.setLoginStatus(QrCodeLoginStatus.EXPIRED);
            return response;
        }
        response = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        return response;
    }

    /**
     * 二维码登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginUser loginByQr(UserLoginRequest request) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), request.getAuthCode());
        if(!redisUtils.hasKey(redisKey)){
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if(!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.CONFIRMED) || StrUtil.isBlank(qrLoginTicketResponse.getUserName())){
            throw new SoException("二维码过期，请刷新");
        }
        UserEntity userEntity = SpringUtil.getBean(UserService.class).queryByUserName(qrLoginTicketResponse.getUserName());
        if(Objects.isNull(userEntity)){
            throw new SoException("用户不存在");
        }
        LoginUser loginUserEntity = new LoginUser();
        loginUserEntity.setId(userEntity.getId());
        loginUserEntity.setLoginType(request.getLoginType().getAccountType());
        return loginUserEntity;
    }


    @Override
    public LoginTokenResponse createToken(LoginUser loginUser) {
        // 设置额外用户信息
        this.setExtraUserInfo(loginUser);
        // 使用saToken登录
        StpUtil.login(loginUser.getId(),loginUser.getLoginType());
        // 获取token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        LoginTokenResponse loginTokenResponse = new LoginTokenResponse();
        loginTokenResponse.setToken(tokenInfo.getTokenValue());
        loginTokenResponse.setExpireTime(tokenInfo.tokenTimeout);
        return loginTokenResponse;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
