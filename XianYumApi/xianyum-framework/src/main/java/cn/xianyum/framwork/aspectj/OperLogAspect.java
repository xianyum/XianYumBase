package cn.xianyum.framwork.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.utils.UserContextHolder;
import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.service.OperLogService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 操作日志切面
 * 基于 @OperLog 注解记录操作日志
 */
@Aspect
@Slf4j
@Order(1)
@Component
public class OperLogAspect {

    @Around("@annotation(cn.xianyum.common.annotation.OperLog) || @within(cn.xianyum.common.annotation.OperLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;
        OperLog[] operLogs = resolveOperLogs(joinPoint);
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            try {
                saveOperLog(joinPoint, operLogs[0], operLogs[1], result, exception, startTime);
            } catch (Exception e) {
                log.error("操作日志保存失败", e);
            }
        }
    }

    private OperLog[] resolveOperLogs(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog methodAnnotation = AnnotationUtils.findAnnotation(method, OperLog.class);
        OperLog classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), OperLog.class);
        return new OperLog[]{methodAnnotation, classAnnotation};
    }

    private String resolveModule(OperLog methodLevel, OperLog classLevel, String fallback) {
        if (methodLevel != null && StrUtil.isNotBlank(methodLevel.module())) {
            return methodLevel.module();
        }
        if (classLevel != null && StrUtil.isNotBlank(classLevel.module())) {
            return classLevel.module();
        }
        return fallback;
    }

    private OperTypeEnum resolveOperType(OperLog methodLevel, OperLog classLevel) {
        if (methodLevel != null) {
            return methodLevel.operType();
        }
        if (classLevel != null) {
            return classLevel.operType();
        }
        return OperTypeEnum.OTHER;
    }

    private String resolveDesc(OperLog methodLevel, OperLog classLevel) {
        if (methodLevel != null && StrUtil.isNotBlank(methodLevel.desc())) {
            return methodLevel.desc();
        }
        if (classLevel != null && StrUtil.isNotBlank(classLevel.desc())) {
            return classLevel.desc();
        }
        return "";
    }

    private boolean resolveSaveParam(OperLog methodLevel, OperLog classLevel) {
        if (methodLevel != null) {
            return methodLevel.saveParam();
        }
        if (classLevel != null) {
            return classLevel.saveParam();
        }
        return true;
    }

    private boolean resolveSaveResult(OperLog methodLevel, OperLog classLevel) {
        if (methodLevel != null) {
            return methodLevel.saveResult();
        }
        if (classLevel != null) {
            return classLevel.saveResult();
        }
        return false;
    }

    private String[] resolveExcludeFields(OperLog methodLevel, OperLog classLevel) {
        if (methodLevel != null) {
            return methodLevel.excludeFields();
        }
        if (classLevel != null) {
            return classLevel.excludeFields();
        }
        return new String[]{"password"};
    }

    private void saveOperLog(ProceedingJoinPoint joinPoint, OperLog methodOperLog, OperLog classOperLog, Object result, Throwable exception, long startTime) {
        long costTime = System.currentTimeMillis() - startTime;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        OperLogEntity entity = new OperLogEntity();
        LoginUser loginUser = UserContextHolder.getLoginUser();
        if (loginUser != null) {
            entity.setUserId(loginUser.getId());
            entity.setUserName(loginUser.getUsername());
        } else {
            entity.setUserId("anonymous");
            entity.setUserName("anonymous");
        }

        String module = resolveModule(methodOperLog, classOperLog,
                joinPoint.getTarget().getClass().getSimpleName().replace("Controller", ""));
        entity.setModule(module);
        entity.setOperType(resolveOperType(methodOperLog, classOperLog).getCode());
        entity.setOperDesc(resolveDesc(methodOperLog, classOperLog));

        // 如果没有接口描述，那就不保存
        if(StrUtil.isBlank(entity.getOperDesc())){
            return;
        }

        if (request != null) {
            entity.setRequestUrl(request.getRequestURI());
            entity.setRequestMethod(request.getMethod());
            String ip = IPUtils.getIpAddr(request);
            entity.setIpAddress(ip);
            entity.setLocation(IPUtils.getIpInfo(ip));
        }

        entity.setClassName(joinPoint.getTarget().getClass().getName());
        entity.setMethodName(method.getName());

        if (resolveSaveParam(methodOperLog, classOperLog)) {
            entity.setRequestParams(buildRequestParams(joinPoint, resolveExcludeFields(methodOperLog, classOperLog)));
        }

        if (resolveSaveResult(methodOperLog, classOperLog) && exception == null) {
            entity.setResponseResult(buildResponseResult(result));
        }

        entity.setCostTime(costTime);

        if (exception != null) {
            entity.setStatus(0);
            entity.setErrorMsg(StrUtil.subPre(exception.getMessage(), 2000));
        } else {
            entity.setStatus(1);
        }

        OperLogService operLogService = SpringUtil.getBean(OperLogService.class);
        operLogService.saveOperLog(entity);
    }

    private String buildRequestParams(ProceedingJoinPoint joinPoint, String[] excludeFields) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                Object arg = args[i];
                if (arg instanceof ServletRequest || arg instanceof ServletResponse) {
                    continue;
                }
                if (arg instanceof MultipartFile multipartFile) {
                    arg = multipartFile.getOriginalFilename();
                }
                params.put(parameterNames[i], arg);
            }
            return desensitize(JSONObject.toJSONString(params), excludeFields);
        } catch (Exception e) {
            log.error("构建请求参数失败", e);
            return "";
        }
    }

    private String buildResponseResult(Object result) {
        if (result == null) {
            return null;
        }
        try {
            String resultStr;
            if (result instanceof String) {
                resultStr = (String) result;
            } else {
                resultStr = JSONObject.toJSONString(result);
            }
            return StrUtil.subPre(resultStr, 3072);
        } catch (Exception e) {
            log.error("构建响应结果失败", e);
            return null;
        }
    }

    private String desensitize(String json, String[] excludeFields) {
        if (StrUtil.isBlank(json)) {
            return json;
        }
        if (excludeFields == null || excludeFields.length == 0) {
            return json;
        }
        String result = json;
        for (String field : excludeFields) {
            if (StrUtil.isBlank(field)) {
                continue;
            }
            String regex = "(?i)(\"[^\"]*" + Pattern.quote(field) + "[^\"]*\")\\s*:\\s*\"[^\"]*\"";
            result = result.replaceAll(regex, "$1:\"***\"");
        }
        return result;
    }
}
