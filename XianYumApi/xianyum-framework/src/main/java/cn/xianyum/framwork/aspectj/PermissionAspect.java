package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {



    /** 定义切点Pointcut */
    @Pointcut("@annotation(cn.xianyum.common.annotation.Permission)")
    public void verify(){
    }

    @Around("@annotation(userPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permission userPermission) throws Throwable {

        return process(pjp,userPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permission userPermission) throws Throwable {
        if(userPermission.publicApi()){
            return pjp.proceed();
        }
        PermissionStrategy strategy = userPermission.strategy();
        switch (strategy) {
            // 放行所有权限，不做任何拦截
            case ALLOW_ALL:
                break;
            // 仅允许管理员身份访问
            case ALLOW_ADMIN:
                return this.processAllowAdminPermission(pjp,userPermission);
                // 仅允许客户端模式进行访问
            case ALLOW_CLIENT:
                return this.processAllowClientPermission(pjp,userPermission);
            default:
        }

        Object result = pjp.proceed();
        return result;
    }

    /**
     * 允许client模式进行访问
     */
    private Object processAllowClientPermission(ProceedingJoinPoint pjp,Permission userPermission) throws Throwable {
        String clientIdField = "clientId";
        String clientSecretField = "clientSecret";
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        String clientId = httpServletRequest.getParameter(clientIdField);
        String clientSecret = httpServletRequest.getParameter(clientSecretField);
        if(StringUtil.isBlank(clientId) || StringUtil.isBlank(clientSecret)){
            return parseResponseClass(userPermission);
        }
        String permissionClientInfo = SystemConstantUtils.getValueByKey("permission_client");
        if(StringUtil.isBlank(permissionClientInfo)){
            return parseResponseClass(userPermission);
        }
        JSONObject permissionClientObj = JSONObject.parseObject(permissionClientInfo);

        String permissionClientId = permissionClientObj.getString(clientIdField);
        String permissionClientSecret = permissionClientObj.getString(clientSecretField);
        if(!(clientId.equals(permissionClientId) && clientSecret.equals(permissionClientSecret))){
            return parseResponseClass(userPermission);
        }
        return pjp.proceed();
    }


    /**
     * 只允许admin/管理员权限访问策略
     *
     * @param pjp
     * @param userPermission
     */
    private Object processAllowAdminPermission(ProceedingJoinPoint pjp, Permission userPermission) throws Throwable {
        if(SecurityUtils.isAdminAuth()){
            return pjp.proceed();
        }
        return parseResponseClass(userPermission);
    }


    /**
     * 解析注解上responseClass，按照类型进行拦截返回
     * @param userPermission
     * @return
     * @throws Exception
     */
    private Object parseResponseClass(Permission userPermission) throws Exception {
        Class<?> rClass = userPermission.responseClass();
        if(rClass == List.class){
            return Results.success(new ArrayList<>());
        }else if(rClass == String.class){
            return Results.success();
        }else if(rClass == PageResponse.class){
            return Results.page(PageResponse.EMPTY_PAGE());
        }else if(rClass == SoException.class){
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }else{
            return Results.success(rClass.getDeclaredConstructor().newInstance());
        }
    }
}
