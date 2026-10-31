package cn.xianyum.system.service.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.*;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.system.entity.response.QrLoginTicketResponse;
import cn.xianyum.system.infra.event.UserOnlineLogEvent;
import cn.xianyum.system.infra.login.LoginStrategy;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.service.UserTokenService;
import com.alibaba.fastjson2.JSONObject;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.util.Map;
import java.util.Objects;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MessageSender messageSender;

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @Resource
    private Map<LoginTypeEnum, LoginStrategy> loginStrategyMap;

    @Override
    public void setExtraUserInfo(LoginUser loginUser) {
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
        String ip = IPUtils.getIpAddr(httpServletRequest);
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(IPUtils.getIpInfo(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
        loginUser.setLoginTime(DateUtil.date());
    }

    @Override
    public void sendLoginCredentials(UserLoginRequest request) {
        if (StrUtil.isBlank(request.getUsername())
                || Objects.isNull(request.getLoginType()) || StrUtil.isBlank(request.getVerifyCode())) {
            throw new SoException("发送失败");
        }
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
        if (!valid) {
            throw new SoException("验证码错误");
        }
        String code = IdGeneratorUtil.getRandomNumber(6);
        switch (request.getLoginType()) {
            case EMAIL -> this.sendLoginCredentialsByEmail(request, code);
            default -> throw new SoException("目前只支持发送邮箱验证码");
        }
        redisUtils.setMin(String.format(RedisKeyEnum.TOKEN_CREDENTIALS_PREFIX.getKey(), request.getUsername()), code, 3);
    }

    @Override
    public void sendLoginCredentialsByEmail(UserLoginRequest request, String code) {
        Context context = new Context();
        context.setVariable("emailCode", code);
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setTitle("XianYum验证码通知");
        messageSenderEntity.setEmailHtmlPath("codeMail");
        messageSenderEntity.setEmailToUser(request.getUsername());
        messageSender.sendAsyncEmailTemplateMessage(MessageCodeEnums.XIANYU_CONFIG_DETAIL_NOTIFY.getMessageCode(), messageSenderEntity, context);
    }

    @Override
    public LoginTokenResponse login(UserLoginRequest request) {
        LoginStrategy strategy = loginStrategyMap.get(request.getLoginType());
        if (Objects.isNull(strategy)) {
            throw new SoException("不支持的登录类型：" + request.getLoginType().getDesc());
        }
        LoginUser loginUser = strategy.login(request);
        LoginTokenResponse response = this.createToken(loginUser);
        SpringUtil.publishEvent(UserOnlineLogEvent.login(loginUser));
        return response;
    }

    @Override
    public QrLoginTicketResponse generateQrCode() {
        QrLoginTicketResponse qrLoginTicketResponse = new QrLoginTicketResponse();
        String qrTicket = IdUtil.getSnowflakeNextIdStr();
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.UNSCANNED);
        qrLoginTicketResponse.setTicket(qrTicket);
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), qrTicket);
        redisUtils.setMin(redisKey, JSONObject.toJSONString(qrLoginTicketResponse), 2);
        return qrLoginTicketResponse;
    }

    @Override
    public void scanQrCode(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        if (!redisUtils.hasKey(redisKey)) {
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if (!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.UNSCANNED)) {
            throw new SoException("二维码已被使用，请刷新");
        }
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.SCANNED);
        redisUtils.setMin(redisKey, JSONObject.toJSONString(qrLoginTicketResponse), 2);
    }

    @Override
    public void confirmQrCode(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        if (!redisUtils.hasKey(redisKey)) {
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if (!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.SCANNED)) {
            throw new SoException("二维码已被使用，请刷新");
        }
        qrLoginTicketResponse.setUserName(UserContextHolder.getLoginUser().getUsername());
        qrLoginTicketResponse.setLoginStatus(QrCodeLoginStatus.CONFIRMED);
        redisUtils.setMin(redisKey, JSONObject.toJSONString(qrLoginTicketResponse), 2);
    }

    @Override
    public QrLoginTicketResponse getQrCodeStatus(String ticket) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), ticket);
        QrLoginTicketResponse response = new QrLoginTicketResponse();
        if (!redisUtils.hasKey(redisKey)) {
            response.setLoginStatus(QrCodeLoginStatus.EXPIRED);
            return response;
        }
        return JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
    }

    @Override
    public LoginTokenResponse createToken(LoginUser loginUser) {
        this.setExtraUserInfo(loginUser);
        StpUtil.login(loginUser.getId(), loginUser.getLoginType());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        LoginTokenResponse loginTokenResponse = new LoginTokenResponse();
        loginTokenResponse.setToken(tokenInfo.getTokenValue());
        loginUser.setToken(tokenInfo.getTokenValue());
        loginTokenResponse.setExpireTime(tokenInfo.tokenTimeout);
        UserContextHolder.setLoginUser(loginUser.getId());
        return loginTokenResponse;
    }

    @Override
    public void logout() {
        String token = StpUtil.getTokenValue();
        SpringUtil.publishEvent(UserOnlineLogEvent.logout());
        StpUtil.logout();
    }
}
