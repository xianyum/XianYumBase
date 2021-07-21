package cn.xianyum.common.exception;

import cn.xianyum.common.utils.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 * @author zhangwei
 * @date 2019/1/31 14:16
 */
@RestControllerAdvice
public class SoExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(SoException.class)
    public DataResult handleRRException(SoException e){
        DataResult result = new DataResult();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());
        return result;
    }


    @ExceptionHandler(Exception.class)
    public DataResult handleException(Exception e){
        logger.error(e.getMessage(), e);
        return DataResult.error();
    }
}
