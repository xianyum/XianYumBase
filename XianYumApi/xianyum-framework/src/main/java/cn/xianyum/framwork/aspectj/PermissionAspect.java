package cn.xianyum.framwork.aspectj;


import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.entity.po.UserEntity;
import com.alibaba.fastjson.JSONObject;
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
    @Pointcut("@annotation(cn.xianyum.common.annotation.Permissions)")
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
        String userJson = (String)redisUtils.get(prefix+token);
        UserEntity userEntity = JSONObject.parseObject(userJson,UserEntity.class);
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
