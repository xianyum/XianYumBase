package cn.xianyum.common.utils;

import cn.xianyum.common.exception.SoException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁工具
 * key 由调用方使用 RedisKeyEnum 拼好完整 key
 *
 * @author xianyum
 */
@Slf4j
@Component
public class RedissonDistributedLockUtils {

    private static final long DEFAULT_WAIT_SECONDS = 3L;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 加锁执行：等待 3 秒，看门狗续期，抢不到抛 429
     */
    public <T> T execute(String key, Supplier<T> supplier) {
        return execute(key, DEFAULT_WAIT_SECONDS, -1L, supplier);
    }

    /**
     * 加锁执行
     *
     * @param key          完整锁 key
     * @param waitSeconds  最长等待秒数，0 表示只尝试一次
     * @param leaseSeconds 固定租约秒数，小于等于 0 时启用看门狗
     */
    public <T> T execute(String key, long waitSeconds, long leaseSeconds, Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier 不能为空");
        if (!tryLock(key, waitSeconds, leaseSeconds)) {
            throw new SoException(HttpStatus.TOO_MANY_REQUESTS.value(), "操作过于频繁，请稍后再试");
        }
        try {
            return supplier.get();
        } finally {
            unlock(key);
        }
    }

    /**
     * 加锁执行无返回值任务
     */
    public void execute(String key, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable 不能为空");
        execute(key, DEFAULT_WAIT_SECONDS, -1L, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 只抢锁，由调用方决定失败后的处理方式
     */
    public boolean tryLock(String key, long waitSeconds, long leaseSeconds) {
        try {
            RLock lock = redissonClient.getLock(key);
            long wait = Math.max(waitSeconds, 0L);
            boolean locked = leaseSeconds > 0
                    ? lock.tryLock(wait, leaseSeconds, TimeUnit.SECONDS)
                    : lock.tryLock(wait, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("获取分布式锁失败, key={}", key);
            }
            return locked;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断, key={}", key, e);
            return false;
        } catch (Exception e) {
            log.error("获取分布式锁异常, key={}", key, e);
            throw new SoException(HttpStatus.SERVICE_UNAVAILABLE.value(), "服务繁忙，请稍后再试");
        }
    }

    /**
     * 释放锁：锁已过期或非当前线程持有时直接忽略
     */
    public void unlock(String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.warn("分布式锁已过期或非当前线程持有, key={}", key);
        } catch (Exception e) {
            log.error("释放分布式锁异常, key={}", key, e);
        }
    }

    /**
     * 判断锁是否被任意线程持有
     */
    public boolean isLocked(String key) {
        return redissonClient.getLock(key).isLocked();
    }
}
