package cn.xianyum.framwork.aspectj;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.base.BaseRequest;
import cn.xianyum.common.entity.base.BaseResponse;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.PermissionThreadLocal;
import cn.xianyum.common.utils.*;
import cn.xianyum.framwork.security.context.PermissionStandardEvaluationContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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


    @Around("@annotation(userPermission)")
    public Object doAround(ProceedingJoinPoint pjp, Permission userPermission) throws Throwable {
        return process(pjp,userPermission);
    }

    public Object process(ProceedingJoinPoint pjp, Permission userPermission) throws Throwable {
        if(userPermission.publicApi()){
            return pjp.proceed();
        }
        // 校验参数值
        this.checkSign(pjp);
        // 权限拦截
        this.checkPermission(userPermission);
        // 数据范围筛选
        this.filterDataScope(userPermission);
        try {
            Object result = pjp.proceed();
            this.setCheckSignField(result,pjp);
            return result;
        }finally {
            PermissionThreadLocal.remove();
        }
    }

    /**
     * 通过反射给签名
     * @param result
     */
    private void setCheckSignField(Object result,ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getName();
        String methodName = signature.getName();
        String methodFullName = className.concat(methodName);
        if(Objects.nonNull(result) && result instanceof Results){
            Results results = (Results)result;
            Object data = results.get("data");
            if(data instanceof BaseResponse){
                Object id = ReflectUtils.getFieldValue(data, "id");
                if(Objects.nonNull(id)){
                    ReflectUtils.setFieldValue(data,"signature",MD5Utils.getMd5(methodFullName.concat(id.toString()),Constants.MD5_DEFAULT_SECRET));
                }
            }
        }
    }


    /**
     * 校验签名
     * @param pjp
     */
    private void checkSign(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getName();
        String methodName = signature.getName();
        String methodFullName = className.concat(methodName);
        Object[] args = pjp.getArgs();
        if(args != null && args.length>0){
            if(args[0] instanceof BaseRequest){
                String checkSign = ReflectUtils.getFieldValue(args[0], "signature");
                Object id = ReflectUtils.getFieldValue(args[0], "id");
                if(StringUtil.isNotEmpty(checkSign) && Objects.nonNull(id)){
                    String md5 = MD5Utils.getMd5(methodFullName.concat(id.toString()),Constants.MD5_DEFAULT_SECRET);
                    if(!checkSign.equals(md5)){
                        throw new SoException(Constants.CHECK_SIGN_MESSAGE);
                    }
                }
            }
        }
    }

    private void filterDataScope(Permission userPermission) {
        DataScopeEnum dataScopeEnum = SecurityUtils.getLoginUser().getDataScopeEnum();
        if(Objects.isNull(dataScopeEnum)){
            return;
        }
        switch (dataScopeEnum){
            case SELF:
                if(!userPermission.ignoreDataScope()){
                    PermissionThreadLocal.set();
                }
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
        if(Boolean.FALSE.equals(value)){
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
    }
}
