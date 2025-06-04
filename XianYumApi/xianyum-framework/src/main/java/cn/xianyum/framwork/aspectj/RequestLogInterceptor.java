package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.service.LogService;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

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

    public static final List<String> FILTER_URLS = Arrays.asList("/xym-extension/v1/ip/getIpInfo");

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
        String requestUri = HttpContextUtils.getHttpServletRequest().getRequestURI();
        String requestParam = getMethodArgs(joinPoint);
        LoginUser loginUser = Optional.ofNullable(SecurityUtils.getLoginUser()).orElse(new LoginUser());
        if(!FILTER_URLS.contains(requestUri)){
            log.info("requestUri:{},cost:{}ms,userId:{}", requestUri, time,loginUser.getId());
            log.info("requestParam:{}",requestParam);
            log.info("responseParam:{}",handlerResult(result));
        }
        //保存日志
        saveSysLog(joinPoint, time,requestParam);
        return result;
    }

    /***
     * 保存日志
     * @param joinPoint
     * @param time
     * @param requestParam
     */
    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, String requestParam) {
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
            // 请求参数
            logEntity.setParams(requestParam);
            //ip
            HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
            String ip = IPUtils.getIpAddr(httpServletRequest);
            logEntity.setIp(ip);
            String ipInfo =IPUtils.getIpInfo(ip);
            logEntity.setIpInfo(ipInfo);
            //用户名
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if(null != loginUser){
                logEntity.setUsername(SecurityUtils.getLoginUser().getUsername());
            }else{
                logEntity.setUsername("system");
            }

            logEntity.setTime(time);
            //异步保存系统日志

            xianYumTaskExecutor.execute(()->{
                SecurityUtils.setLoginUser(loginUser);
                SpringUtils.getBean(LogService.class).saveLog(logEntity);
            });
        }
    }

    /**
     * 获取请求参数
     * @param point
     * @return
     */
    private String getMethodArgs(JoinPoint point) {
        Object[] args = point.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        try {
            Map<String, Object> params = new HashMap<>();
            String[] parameterNames = ((MethodSignature) point.getSignature()).getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                Object arg = args[i];
                // 过滤不能转换成JSON的参数
                if ((arg instanceof ServletRequest) || (arg instanceof ServletResponse)) {
                    continue;
                } else if ((arg instanceof MultipartFile)) {
                    arg = arg.toString();
                }
                params.put(parameterNames[i], arg);
            }
            return JSONObject.toJSONString(params);
        } catch (Exception e) {
            log.error("接口出入参日志打印切面处理请求参数异常", e);
        }
        return Arrays.toString(args);
    }


    /**
     * 返回结果简单处理
     * 1）把返回结果转成String，方便输出。
     * 2）返回结果太长则截取（最多3072个字符），方便展示。
     *
     * @param result 原方法调用的返回结果
     * @return 处理后的
     */
    private String handlerResult(Object result) {
        if (result == null) {
            return null;
        }
        String resultStr;
        try {
            if (result instanceof String) {
                resultStr = (String) result;
            } else {
                resultStr = JSONObject.toJSONString(result);// 如果返回结果非String类型，转换成JSON格式的字符串
            }
            if (resultStr.length() > 3072) {
                resultStr = resultStr.substring(0, 3072);
            }
        } catch (Exception e) {
            resultStr = result.toString();
            log.error("接口出入参日志打印切面处理返回参数异常", e);
        }
        return resultStr;
    }

}
