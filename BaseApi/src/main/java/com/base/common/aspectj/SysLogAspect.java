package com.base.common.aspectj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.common.annotation.SysLog;
import com.base.common.utils.*;
import com.base.entity.po.LogEntity;
import com.base.entity.request.UserRequest;
import com.base.service.iservice.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 系统日志，切面处理类
 * @author zhangwei
 * @date 2019/4/18 14:18
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.ip.info}")
    private String redisIpInfo;

    @Autowired
    private LogService logService;

    @Pointcut("@annotation(com.base.common.annotation.SysLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(point, time);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogEntity logEntity = new LogEntity();
        logEntity.setId(UUIDUtils.UUIDReplace());
        SysLog syslog = method.getAnnotation(SysLog.class);
        if(syslog != null){
            //注解上的描述
            logEntity.setOperation(syslog.value());
        }
        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        logEntity.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            if(args != null && args.length>0){
                if(args[0] instanceof UserRequest){
                    UserRequest userRequest = (UserRequest)args[0];
                    userRequest.setPassword("");
                }
            }
            String params = JSON.toJSONString(args);
            logEntity.setParams(params);
        }catch (Exception e){

        }

        //ip
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        String ip = IPUtils.getIpAddr(httpServletRequest);
        logEntity.setIp(ip);

        String ipInfo = (String)redisUtils.get(redisIpInfo+ip);
        if(StringUtil.isEmpty(ipInfo)){
            ipInfo = IPUtils.getIpInfo(ip);
            if(!ipInfo.contains("未知")){
                redisUtils.set(redisIpInfo+ip,ipInfo);
            }
        }
        logEntity.setIpInfo(ipInfo);
        //用户名
        logEntity.setUsername( AuthUserToken.getUser().getUsername());

        logEntity.setTime(time);
        logEntity.setCreateTime(new Date());

        //保存系统日志
        logService.saveLog(logEntity);
    }
}
