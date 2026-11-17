package cn.xianyum.system.infra.handler;

import cn.dev33.satoken.apikey.error.SaApiKeyErrorCode;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异常处理器
 * @author zhangwei
 * @date 2019/1/31 14:16
 */
@RestControllerAdvice
public class SystemExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSender messageSender;


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(SoException.class)
    public Results handleRRException(SoException e){
        Results result = new Results();
        result.put(Constants.ERROR_CODE_FIELD, e.getCode());
        result.put(Constants.ERROR_MSG_FIELD, e.getMessage());
        return result;
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public Results handleMysqlDuplicateException(DataIntegrityViolationException e){
        logger.error("",e);
        Results result = new Results();
        result.put(Constants.ERROR_CODE_FIELD, Constants.SERVER_ERROR_STATUS_CODE);
        result.put(Constants.ERROR_MSG_FIELD, e.getMessage());
        return result;
    }

    /**
     * 处理 API Key 异常（包含 ApiKeyScopeException）
     * code 对应 SaApiKeyErrorCode：12301~12305、12311、12312
     */
    @ExceptionHandler(ApiKeyException.class)
    public Results handleApiKeyException(ApiKeyException e){
        String message;
        if(e.getCode() == SaApiKeyErrorCode.CODE_12301) {
            message = "ApiKey无效，请检查后重试";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12302) {
            message = "ApiKey已过期";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12303) {
            message = "ApiKey已被禁用";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12304) {
            message = "ApiKey参数校验未通过";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12305) {
            message = "ApiKey索引记录功能未开启";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12311) {
            message = "当前接口不允许此ApiKey访问";
        } else if(e.getCode() == SaApiKeyErrorCode.CODE_12312) {
            message = "ApiKey与当前用户不匹配";
        } else {
            message = "ApiKey校验失败";
        }
        logger.error("",e);
        return Results.error(HttpStatus.UNAUTHORIZED.value(),message);
    }


    /**
     * 处理未登录异常
     * @param nle
     * @return
     */
    @ExceptionHandler(NotLoginException.class)
    public Results handlerNotLoginException(NotLoginException nle) {
        String message;
        if(nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "当前用户未登录";
        } else if(nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "当前用户认证无效";
        } else if(nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "当前用户认证已过期";
        } else if(nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "当前用户已被顶下线";
        } else if(nle.getType().equals(NotLoginException.KICK_OUT)) {
            message = "当前用户已被踢下线";
        } else if(nle.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "当前用户已被冻结";
        } else if(nle.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "token未指定前缀";
        }else {
            message = "当前用户未登录";
        }
        logger.error("",nle);
        return Results.error(HttpStatus.UNAUTHORIZED.value(),message);
    }

    /**
     * 没有权限操作
     * @param e
     * @return
     */
    @ExceptionHandler(NotPermissionException.class)
    public Results handleRRException(NotPermissionException e){
        logger.error("",e);
        return Results.error(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    public Results handleException(Exception e){
        this.sendFailedExceptionMessage(e);
        logger.error("",e);
        return Results.error();
    }


    /**
     * 发送异常信息
     * @param e
     */
    public void sendFailedExceptionMessage(Exception e){
        String exceptionType = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        Map<String,Object> exceptionPositionMap = new LinkedHashMap<>();
        exceptionPositionMap.put("异常原因：",exceptionMessage);
        exceptionPositionMap.put("异常类：",exceptionType);
        exceptionPositionMap.put("发生时间：", DateTime.now().toString(DateUtils.DATE_TIME_PATTERN));
        StackTraceElement[] stackTrace = e.getStackTrace();
        for(int i = 0;i<stackTrace.length;i++){
            if(i < 3){
                StackTraceElement stackTraceElement = stackTrace[i];
                String key = "异常位置"+(i+1)+"：";
                String exceptionPosition = stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()
                        +"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")";
                exceptionPositionMap.put(key,exceptionPosition);
            }else{
                break;
            }
        }
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(exceptionPositionMap));
        this.messageSender.sendAsyncMessage(MessageCodeEnums.SYSTEM_EXCEPTION_NOTIFY.getMessageCode(),messageSenderEntity);
    }
}
