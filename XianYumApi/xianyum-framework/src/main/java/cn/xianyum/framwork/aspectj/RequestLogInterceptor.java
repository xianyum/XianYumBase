package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.LogService;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/12/14 21:19
 * @Email wei.zhang@raipiot.com
 */
@Aspect
@Slf4j
@Order(1)
@Component
public class RequestLogInterceptor {

    @Autowired
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Pointcut("execution(* cn.xianyum.*.controller.*.*(..))")
    private void executeRequestLog() {
    }

    @Around("executeRequestLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(joinPoint, time);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if(Objects.nonNull(apiOperation)) {
            LogEntity logEntity = new LogEntity();
            logEntity.setOperation(apiOperation.value());
            //请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = methodSignature.getName();
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
                String params = JSONObject.toJSONString(args);
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
}
