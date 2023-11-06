package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.LogService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


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
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Pointcut("@annotation(cn.xianyum.common.annotation.SysLog)")
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
        String ipInfo =IPUtils.getIpInfo(ip);
        logEntity.setIpInfo(ipInfo);
        //用户名
        if(null != SecurityUtils.getLoginUser()){
            logEntity.setUsername(SecurityUtils.getLoginUser().getUsername());
        }else{
            logEntity.setUsername("system");
        }


        logEntity.setTime(time);
        //异步保存系统日志
        xianYumTaskExecutor.execute(()->{
            SpringUtils.getBean(LogService.class).saveLog(logEntity);
        });
    }
}
