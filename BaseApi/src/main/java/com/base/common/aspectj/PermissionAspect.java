package com.base.common.aspectj;

import com.base.common.annotation.Permissions;
import com.base.common.exception.SoException;
import com.base.common.utils.RedisUtils;
import com.base.common.utils.StringUtil;
import com.base.entity.enums.PermissionEnum;
import com.base.entity.po.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;



/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Value("${redis.token.prefix:token}")
    private String prefix;

    @Autowired
    private RedisUtils redisUtils;

    /** 定义切点Pointcut */
    @Pointcut("@annotation(com.base.common.annotation.Permissions)")
    public void verify(){
    }

    @Around("@annotation(adminPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        return process(pjp,adminPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permissions adminPermission) throws Throwable {

        String[] value = adminPermission.value();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest httpRequest = sra.getRequest();
        String token = httpRequest.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(StringUtil.isEmpty(token)){
            token = httpRequest.getParameter("token");
        }
        UserEntity userEntity = (UserEntity)redisUtils.get(prefix+token);
        String authPermission = PermissionEnum.getNameByStatus(userEntity.getPermission());
        if(value != null){
            for(String permission : value){
                if(authPermission.equals(permission)){
                    if(PermissionEnum.VISITOR.getName().equals(permission)){
                        throw new SoException("对不起，您是游客身份没有权限操作！");
                    }else{
                        throw new SoException("对不起，您没有权限操作！");
                    }
                }
            }
        }
        Object result = pjp.proceed();
        return result;
    }

}
