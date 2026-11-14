package cn.xianyum.framwork.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.annotation.BusinessLock;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.MD5Utils;
import cn.xianyum.common.utils.RedissonDistributedLockUtils;
import cn.xianyum.common.utils.UserContextHolder;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务锁切面
 * 基于 @BusinessLock 与 Redisson 分布式锁，防止重复提交
 * 锁 key：XianYumApi:business-lock:{prefix}:{userId}:{digest}
 */
@Slf4j
@Aspect
@Component
@Order(0)
public class BusinessLockAspect {

    private static final long DEFAULT_LEASE_SECONDS = 10L;

    private final RedissonDistributedLockUtils redissonLockUtils;

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public BusinessLockAspect(RedissonDistributedLockUtils redissonLockUtils) {
        this.redissonLockUtils = redissonLockUtils;
    }

    @Around("@annotation(businessLock)")
    public Object around(ProceedingJoinPoint pjp, BusinessLock businessLock) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String redisKey = buildKey(pjp, signature, businessLock);
        long leaseSeconds = businessLock.leaseTime() > 0 ? businessLock.leaseTime() : DEFAULT_LEASE_SECONDS;
        if (!redissonLockUtils.tryLock(redisKey, 0, leaseSeconds)) {
            log.warn("BusinessLock 拦截重复提交, key={}, method={}", redisKey, method.toGenericString());
            throw new SoException(HttpStatus.TOO_MANY_REQUESTS.value(), businessLock.message());
        }
        try {
            return pjp.proceed();
        } finally {
            redissonLockUtils.unlock(redisKey);
        }
    }

    /**
     * 组装锁 key
     */
    private String buildKey(ProceedingJoinPoint pjp, MethodSignature signature, BusinessLock businessLock) {
        String prefix = StrUtil.isNotBlank(businessLock.prefix())
                ? businessLock.prefix() : defaultPrefix(pjp, signature);
        StringBuilder key = new StringBuilder(RedisKeyEnum.BUSINESS_LOCK.getKey()).append(prefix);
        if (businessLock.includeUser()) {
            key.append(':').append(loginUserId());
        }
        String digest = buildDigest(pjp, signature, businessLock);
        if (StrUtil.isNotBlank(digest)) {
            key.append(':').append(digest);
        }
        return key.toString();
    }

    /**
     * 未指定业务前缀时，使用 "类名#方法名" 保证唯一性
     */
    private String defaultPrefix(ProceedingJoinPoint pjp, MethodSignature signature) {
        return pjp.getTarget().getClass().getName() + "#" + signature.getName();
    }

    /**
     * 未登录时返回空串
     */
    private String loginUserId() {
        LoginUser loginUser = UserContextHolder.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            return "";
        }
        return loginUser.getId();
    }

    /**
     * 生成请求摘要：优先使用 SpEL 表达式，其次使用参数摘要
     */
    private String buildDigest(ProceedingJoinPoint pjp, MethodSignature signature, BusinessLock businessLock) {
        if (StrUtil.isNotBlank(businessLock.key())) {
            return evaluateKeyExpression(pjp, signature, businessLock.key());
        }
        if (!businessLock.includeArgs()) {
            return "";
        }
        Object[] args = pjp.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        List<Object> effectiveArgs = new ArrayList<>(args.length);
        for (Object arg : args) {
            if (!isExcludedArg(arg)) {
                effectiveArgs.add(arg);
            }
        }
        if (effectiveArgs.isEmpty()) {
            return "";
        }
        return MD5Utils.getMd5(toJson(effectiveArgs));
    }

    /**
     * 解析 SpEL，支持 #p0 与 #参数名 两种写法
     */
    private String evaluateKeyExpression(ProceedingJoinPoint pjp, MethodSignature signature, String expression) {
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = pjp.getArgs();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            if (parameterNames != null && i < parameterNames.length && parameterNames[i] != null) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        Object value = expressionParser.parseExpression(expression).getValue(context);
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * 序列化参数，失败时退化为 toString
     */
    private String toJson(List<Object> args) {
        try {
            return JSON.toJSONString(args);
        } catch (Exception e) {
            log.warn("BusinessLock 参数序列化失败，使用 toString 生成摘要", e);
            return args.toString();
        }
    }

    /**
     * 排除与业务无关、序列化不稳定的参数
     */
    private boolean isExcludedArg(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof HttpSession
                || arg instanceof MultipartFile
                || arg instanceof BindingResult;
    }
}
