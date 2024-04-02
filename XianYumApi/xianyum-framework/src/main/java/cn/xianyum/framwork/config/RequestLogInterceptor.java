package cn.xianyum.framwork.config;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/12/14 21:19
 * @Email wei.zhang@raipiot.com
 */
@Aspect
@Slf4j
@Order(1)
public class RequestLogInterceptor {

    @Pointcut("execution(* cn.xianyum.analysis.controller.IpController.*(..))")
    private void executeRequestLog() {
    }

    @Around("executeRequestLog()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //请求的参数
        Object[] args = point.getArgs();
        log.info("日志：{}", JSONObject.toJSONString(args));
        return point.proceed();
    }
}
