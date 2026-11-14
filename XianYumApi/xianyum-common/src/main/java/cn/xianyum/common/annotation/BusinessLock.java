package cn.xianyum.common.annotation;

import java.lang.annotation.*;

/**
 * 业务锁注解
 * 基于 Redisson 分布式锁，防止同一业务在短时间内被重复触发
 * 锁 key 固定前缀：XianYumApi:business-lock:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessLock {

    /**
     * 业务标识，拼接在固定前缀之后
     * 例：sys:user:add；为空时默认使用 "类名#方法名"
     */
    String prefix() default "";

    /**
     * 定位业务参数的 SpEL 表达式（单个）
     * 例：#p0.orderId、#request.orderId
     * 非空时优先于 includeArgs 的参数摘要
     */
    String key() default "";

    /**
     * key 为空时是否把方法参数摘要纳入锁 key
     * 默认 true：同用户 + 同接口 + 同参数才视为重复提交
     */
    boolean includeArgs() default true;

    /**
     * 是否把用户维度纳入锁 key
     * true：同一用户 + 前缀 + 参数互斥；未登录时用户段留空
     * false：不带用户维度，所有调用方按前缀 + 参数全局互斥
     */
    boolean includeUser() default false;

    /**
     * 锁租约时长（秒），固定过期、不启用看门狗
     * 方法结束立即释放；需大于业务最大耗时，作为异常未释放时的兜底
     */
    long leaseTime() default 10;

    /**
     * 抢锁失败时的提示语
     */
    String message() default "操作过于频繁，请勿重复提交";
}
