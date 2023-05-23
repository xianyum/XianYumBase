package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {


    /** 定义切点Pointcut */
    @Pointcut("@annotation(cn.xianyum.common.annotation.Permissions)")
    public void verify(){
    }

    @Around("@annotation(adminPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        return process(pjp,adminPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        PermissionStrategy strategy = adminPermission.strategy();
        LoginUser userEntity = SecurityUtils.getLoginUser();
        String authPermission = PermissionEnum.getNameByStatus(userEntity.getPermission());
        switch (strategy) {
            // 放行所有权限，不做任何拦截
            case ALLOW_ALL:
                break;
            // 仅允许管理员身份访问
            case ALLOW_ADMIN:
                this.processAllowAdminPermission(authPermission);
                break;
            default:
        }

        Object result = pjp.proceed();
        return result;
    }


    /**
     * 只允许admin/管理员权限访问策略
     * @param authPermission
     */
    private void processAllowAdminPermission(String authPermission) {
        SecurityUtils.allowAdminAuth();
    }

}
