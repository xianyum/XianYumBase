package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private RedisUtils redisUtils;

    /** 定义切点Pointcut */
    @Pointcut("@annotation(cn.xianyum.common.annotation.Permissions)")
    public void verify(){
    }

    @Around("@annotation(adminPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        return process(pjp,adminPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        String[] value = adminPermission.value();
        LoginUser userEntity = SecurityUtils.getLoginUser();
        String authPermission = PermissionEnum.getNameByStatus(userEntity.getPermission());
        if(value != null){
            for(String permission : value){
                if(authPermission.equals(permission)){
                    if(PermissionEnum.VISITOR.getName().equals(permission)){
                        throw new SoException(HttpStatus.UNAUTHORIZED.value(),"对不起，您是游客身份没有权限操作！");
                    }else{
                        throw new SoException(HttpStatus.UNAUTHORIZED.value(),"对不起，您没有权限操作！");
                    }
                }
            }
        }
        Object result = pjp.proceed();
        return result;
    }

}
