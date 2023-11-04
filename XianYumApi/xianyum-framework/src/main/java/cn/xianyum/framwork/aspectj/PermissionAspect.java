package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.PermissionThreadLocal;
import cn.xianyum.common.utils.*;
import cn.xianyum.framwork.security.context.PermissionStandardEvaluationContext;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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

    @Autowired
    private PermissionStandardEvaluationContext permissionStandardEvaluationContext;

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
        // 权限拦截
        this.checkPermission(userPermission);
        // 数据范围筛选
        this.filterDataScope(userPermission);

        try {
            Object result = pjp.proceed();
            return result;
        }finally {
            PermissionThreadLocal.remove();
        }
    }

    private void filterDataScope(Permission userPermission) {
        DataScopeEnum dataScopeEnum = SecurityUtils.getLoginUser().getDataScopeEnum();
        if(Objects.isNull(dataScopeEnum)){
            return;
        }
        switch (dataScopeEnum){
            case SELF:
                PermissionThreadLocal.set();
                break;
            case VISITOR:
                this.processVisitorDataScope(userPermission);
                break;
            default:
                break;
        }

    }

    /**
     * 处理游客数据权限
     */
    private void processVisitorDataScope(Permission userPermission) {
        //拦截post，put，delete请求不得操作
        String lowerCase = HttpContextUtils.getHttpServletRequest().getMethod().toLowerCase();
        if (Constants.HTTP_POST_METHOD.equals(lowerCase) || Constants.HTTP_PUT_METHOD.equals(lowerCase)
                || Constants.HTTP_DELETE_METHOD.equals(lowerCase)) {
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
        if(!userPermission.ignoreDataScope()){
            PermissionThreadLocal.set();
        }
    }

    private void checkPermission(Permission userPermission) {
        // 权限校验
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(userPermission.value());
        Boolean value = expression.getValue(permissionStandardEvaluationContext,Boolean.class);
        log.info("校验用户的授权信息：{},value={}",userPermission.value(),value);
        if(Boolean.FALSE.equals(value)){
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
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
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
        String permissionClientInfo = SystemConstantUtils.getValueByKey("permission_client");
        if(StringUtil.isBlank(permissionClientInfo)){
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
        JSONObject permissionClientObj = JSONObject.parseObject(permissionClientInfo);

        String permissionClientId = permissionClientObj.getString(clientIdField);
        String permissionClientSecret = permissionClientObj.getString(clientSecretField);
        if(!(clientId.equals(permissionClientId) && clientSecret.equals(permissionClientSecret))){
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
        return pjp.proceed();
    }
}
