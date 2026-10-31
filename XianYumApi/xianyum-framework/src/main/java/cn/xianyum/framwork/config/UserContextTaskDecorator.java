package cn.xianyum.framwork.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.UserContextHolder;
import org.springframework.core.task.TaskDecorator;
import java.util.Objects;

/**
 * 用户上下文装饰器
 * @author xianyum
 * @date 2026/10/31 21:37
 */
public class UserContextTaskDecorator implements TaskDecorator {

    /**
     * Decorate the given {@code Runnable}, returning a potentially wrapped
     * {@code Runnable} for actual execution, internally delegating to the
     * original {@link Runnable#run()} implementation.
     *
     * @param runnable the original {@code Runnable}
     * @return the decorated {@code Runnable}
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        // 【提交任务时：主线程】捕获当前用户上下文
        LoginUser loginUser = UserContextHolder.getLoginUser();

        return () -> {
            try {
                if (Objects.nonNull(loginUser)) {
                    UserContextHolder.setLoginUser(loginUser);
                }
                runnable.run();
            } finally {
                // 【子线程执行完强制清理，防止线程复用串数据】
                UserContextHolder.clear();
            }
        };
    }
}
