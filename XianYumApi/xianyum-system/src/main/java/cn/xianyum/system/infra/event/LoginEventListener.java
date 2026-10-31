package cn.xianyum.system.infra.event;

import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.enums.LoginEventStatusEnum;
import cn.xianyum.system.dao.UserOnlineMapper;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 登录事件监听器
 * - SUCCESS：将在线用户信息写入/更新数据库
 * - FAIL：记录失败日志（可扩展为失败次数统计、封禁等）
 */
@Slf4j
@Component
public class LoginEventListener {

    @Value("${redis.token.expire:30}")
    private Integer expire;

    @Resource
    private UserOnlineMapper userOnlineMapper;

    @Async
    @EventListener
    public void handleLoginEvent(LoginEvent event) {
        if (LoginEventStatusEnum.SUCCESS.equals(event.status())) {
            handleLoginSuccess(event);
        } else if (LoginEventStatusEnum.FAIL.equals(event.status())) {
            handleLoginFail(event);
        }
    }

    private void handleLoginSuccess(LoginEvent event) {
        try {
            if (StrUtil.isBlank(event.token())) {
                return;
            }
            // 先清理已过期记录
            userOnlineMapper.deleteExpired();

            // 同一 token 可能已存在（旧会话残留），先删
            UserOnlineEntity existed = userOnlineMapper.selectByToken(event.token());
            if (existed != null) {
                userOnlineMapper.deleteById(existed.getId());
            }

            // 主键用 token，保证多设备登录不冲突（同一个用户可以有多个不同的 token）
            UserOnlineEntity entity = new UserOnlineEntity();
            entity.setId(event.token());
            entity.setToken(event.token());
            entity.setUsername(event.username());
            entity.setIpaddr(event.ipaddr());
            entity.setLoginLocation(event.loginLocation());
            entity.setBrowser(event.browser());
            entity.setOs(event.os());
            entity.setLoginType(event.loginType());
            entity.setLoginTime(event.loginTime());
            entity.setExpireTime(new Date(System.currentTimeMillis() + expire * 60_000L));
            userOnlineMapper.insert(entity);

            log.info("【登录事件处理】写入在线用户成功, username={}, token={}", event.username(), event.token());
        } catch (Exception e) {
            log.error("【登录事件处理】写入在线用户失败, username={}, token={}", event.username(), event.token(), e);
        }
    }

    private void handleLoginFail(LoginEvent event) {
        try {
            log.warn("【登录事件处理】登录失败, username={}, ip={}, reason={}",
                    event.username(), event.ipaddr(), event.failReason());
        } catch (Exception e) {
            log.error("【登录事件处理】登录失败处理异常", e);
        }
    }
}
