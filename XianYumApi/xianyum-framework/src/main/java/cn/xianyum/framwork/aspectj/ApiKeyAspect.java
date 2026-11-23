package cn.xianyum.framwork.aspectj;

import cn.dev33.satoken.apikey.template.SaApiKeyUtil;
import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * API Key 切面
 * 拦截被 @SaCheckApiKey 标注的接口，统一处理 API Key 相关的增强逻辑
 */
@Aspect
@Slf4j
@Order(2)
@Component
public class ApiKeyAspect {

    /** 请求量日统计保留 7 天 */
    private static final long REQUEST_COUNT_EXPIRE_SECONDS = 7 * 24 * 3600L;

    @Resource
    private RedisUtils redisUtils;

    @Around("@annotation(cn.dev33.satoken.apikey.annotation.SaCheckApiKey) || @within(cn.dev33.satoken.apikey.annotation.SaCheckApiKey)")
    public Object aroundApiKeyRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            handleApiKeyAfterRequest();
        }
    }

    /**
     * 请求完成后的统一处理入口，后续新增 API Key 相关能力在此扩展
     */
    private void handleApiKeyAfterRequest() {
        try {
            String apiKey = resolveApiKey();
            if (StrUtil.isBlank(apiKey)) {
                return;
            }
            recordRequestCount(apiKey);
        } catch (Exception e) {
            log.error("ApiKey请求后处理失败", e);
        }
    }

    /**
     * 从当前请求中解析 API Key
     */
    private String resolveApiKey() {
        return SaApiKeyUtil.readApiKeyValue(SaHolder.getRequest());
    }

    /**
     * 记录 API Key 当日请求量
     */
    private void recordRequestCount(String apiKey) {
        String statKey = RedisKeyEnum.API_KEY_DAY_STAT.formatKey(DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT));
        double count = redisUtils.hIncr(statKey, apiKey, 1);
        if (count == 1) {
            redisUtils.expire(statKey, REQUEST_COUNT_EXPIRE_SECONDS);
        }
    }
}
