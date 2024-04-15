package cn.xianyum.system.infra.handler;

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



    /**
     * 处理MYSQL唯一索引异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Results handleMysqlDuplicateException(DataIntegrityViolationException e){
        Results result = new Results();
        result.put(Constants.ERROR_CODE_FIELD, Constants.SERVER_ERROR_STATUS_CODE);
        result.put(Constants.ERROR_MSG_FIELD, Constants.SERVER_ERROR_SQL_DUPLICATE_MSG);
        return result;
    }



    @ExceptionHandler(Exception.class)
    public Results handleException(Exception e){
        this.sendFailedExceptionMessage(e);
        logger.error(e.getMessage(), e);
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
