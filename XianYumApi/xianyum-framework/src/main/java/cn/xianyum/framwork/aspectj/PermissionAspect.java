package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.XianYumConstant;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.HttpContextUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.SystemConstantUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


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

    @Around("@annotation(adminPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permission adminPermission) throws Throwable {

        return process(pjp,adminPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permission adminPermission) throws Throwable {

        PermissionStrategy strategy = adminPermission.strategy();
        switch (strategy) {
            // 放行所有权限，不做任何拦截
            case ALLOW_ALL:
                break;
            // 仅允许管理员身份访问
            case ALLOW_ADMIN:
                this.processAllowAdminPermission(this.getAuthPermissionByLoginUser());
                break;
                // 仅允许客户端模式进行访问
            case ALLOW_CLIENT:
                this.processAllowClientPermission();
                break;
            default:
        }

        Object result = pjp.proceed();
        return result;
    }

    /**
     * 获取当前登录用户权限标识符
     * @return
     */
    private String getAuthPermissionByLoginUser() {
        LoginUser userEntity = SecurityUtils.getLoginUser();
        String authPermission = "";
        if(Objects.nonNull(userEntity)){
            authPermission = PermissionEnum.getNameByStatus(userEntity.getPermission());
        }
        return authPermission;
    }

    /**
     * 允许client模式进行访问
     */
    private void processAllowClientPermission() {
        String clientIdField = "clientId";
        String clientSecretField = "clientSecret";
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        String clientId = httpServletRequest.getParameter(clientIdField);
        String clientSecret = httpServletRequest.getParameter(clientSecretField);
        if(StringUtil.isBlank(clientId) || StringUtil.isBlank(clientSecret)){
            throw new SoException(HttpStatus.FORBIDDEN.value(), XianYumConstant.Message.NO_PERMISSION_MESSAGE);
        }

        String permissionClientInfo = SystemConstantUtils.getValueByKey("permission_client");
        if(StringUtil.isBlank(permissionClientInfo)){
            throw new SoException(HttpStatus.FORBIDDEN.value(),XianYumConstant.Message.NO_PERMISSION_MESSAGE);
        }
        JSONObject permissionClientObj = JSONObject.parseObject(permissionClientInfo);

        String permissionClientId = permissionClientObj.getString(clientIdField);
        String permissionClientSecret = permissionClientObj.getString(clientSecretField);
        if(!(clientId.equals(permissionClientId) && clientSecret.equals(permissionClientSecret))){
            throw new SoException(HttpStatus.FORBIDDEN.value(),XianYumConstant.Message.NO_PERMISSION_MESSAGE);
        }
    }


    /**
     * 只允许admin/管理员权限访问策略
     * @param authPermission
     */
    private void processAllowAdminPermission(String authPermission) {
        SecurityUtils.allowAdminAuth();
    }

}
