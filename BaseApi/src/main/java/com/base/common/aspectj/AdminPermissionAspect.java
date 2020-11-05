package com.base.common.aspectj;

import com.base.common.annotation.AdminPermission;
import com.base.common.exception.SoException;
import com.base.common.utils.AuthUserToken;
import com.base.entity.enums.PermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class AdminPermissionAspect {

    @Value("${redis.permission.admin:false}")
    private boolean isAdminPermission;

    /** 定义切点Pointcut */
    @Pointcut("@annotation(com.base.common.annotation.AdminPermission)")
    public void verify(){
    }

    @Around("@annotation(adminPermission)")
    public Object doAround(ProceedingJoinPoint pjp, AdminPermission adminPermission) throws Throwable {

        return process(pjp,adminPermission);
    }

    public Object process(ProceedingJoinPoint pjp, AdminPermission adminPermission) throws Throwable {

//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//        HttpServletRequest request = sra.getRequest();
        if(isAdminPermission){
            Integer permission = AuthUserToken.getUser().getPermission();
            if(permission == null || PermissionEnum.ADMIN.getStatus() != permission){
                throw new SoException("对不起，您没有权限操作！");
            }
        }
        Object result = pjp.proceed();
        return result;
    }

}
