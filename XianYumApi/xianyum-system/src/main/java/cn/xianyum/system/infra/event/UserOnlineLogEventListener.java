package cn.xianyum.system.infra.event;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginEventStatusEnum;
import cn.xianyum.common.utils.IdGeneratorUtil;
import cn.xianyum.common.utils.UserContextHolder;
import cn.xianyum.system.dao.UserOnlineMapper;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * 用户在线状态事件监听器
 * - LOGIN  ：将在线用户信息写入数据库
 * - LOGOUT ：从数据库删除在线用户记录
 */
@Slf4j
@Component
public class UserOnlineLogEventListener {

    @Resource
    private UserOnlineMapper userOnlineMapper;

    @Async("xianYumTaskExecutor")
    @EventListener
    public void handleEvent(UserOnlineLogEvent event) {
        if (LoginEventStatusEnum.LOGIN.equals(event.status())) {
            handleLogin(event.loginUser());
        } else if (LoginEventStatusEnum.LOGOUT.equals(event.status())) {
            handleLogout();
        }
    }

    /**
     * 处理登录事件
     * - 登录时，将用户信息写入数据库
     * - 登录时，设置 SaToken 上下文
     * @param loginUser
     */
    private void handleLogin(LoginUser loginUser) {
        try {
            if (Objects.isNull(loginUser)) {
                return;
            }
            userOnlineMapper.deleteExpired();
            SaTokenContextMockUtil.setMockContext(()->{
                StpUtil.setTokenValueToStorage(loginUser.getToken());
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                UserOnlineEntity entity = new UserOnlineEntity();
                entity.setId(IdGeneratorUtil.nextId());
                entity.setToken(loginUser.getToken());
                entity.setUsername(loginUser.getUsername());
                entity.setIpaddr(loginUser.getIpaddr());
                entity.setLoginLocation(loginUser.getLoginLocation());
                entity.setBrowser(loginUser.getBrowser());
                entity.setOs(loginUser.getOs());
                entity.setLoginType(loginUser.getLoginType());
                entity.setLoginTime(loginUser.getLoginTime());
                entity.setExpireTime(DateUtil.date(loginUser.getLoginTime().getTime() + tokenInfo.getTokenTimeout() * 1000));
                userOnlineMapper.insert(entity);
            });
            log.info("【在线事件】写入在线用户, username={}, token={}", loginUser.getUsername(), loginUser.getToken());
        } catch (Exception e) {
            log.error("【在线事件】写入在线用户失败, username={}, token={}", loginUser.getUsername(), loginUser.getToken(), e);
        }
    }

    /**
     * 处理退出事件
     * - 退出时，从数据库删除在线用户记录
     */
    private void handleLogout() {
        try {
            LoginUser loginUser = UserContextHolder.getLoginUser();
            log.info("【在线事件】删除在线用户, username={}, token={}", loginUser.getUsername(), loginUser.getToken());
            if (Objects.nonNull(loginUser) && StrUtil.isNotBlank(loginUser.getToken())) {
                userOnlineMapper.deleteByToken(loginUser.getToken());
            }
        } catch (Exception e) {
            log.error("【在线事件】删除在线用户失败", e);
        }
    }
}
