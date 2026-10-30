package cn.xianyum.framwork.aspectj;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.xianyum.common.annotation.IgnoreDataScope;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.base.BaseRequest;
import cn.xianyum.common.entity.base.BaseResponse;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.PermissionThreadLocal;
import cn.xianyum.common.utils.*;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2020/11/5 21:15
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(saCheckPermission)")
    public Object doAround(ProceedingJoinPoint pjp, SaCheckPermission saCheckPermission) throws Throwable {
        return process(pjp,saCheckPermission);
    }

    public Object process(ProceedingJoinPoint pjp, SaCheckPermission saCheckPermission) throws Throwable {
        // 校验参数值
        this.checkSign(pjp);
        // 解析是否要忽略数据范围筛选
        boolean ignoreDataScope = parseIgnoreDataScope(pjp);
        // 数据范围筛选
        this.filterDataScope(ignoreDataScope);
        try {
            Object result = pjp.proceed();
            this.setCheckSignField(result,pjp);
            return result;
        }finally {
            PermissionThreadLocal.remove();
        }
    }

    /**
     * 解析是否要忽略数据范围筛选
     * @param pjp
     * @return
     */
    private boolean parseIgnoreDataScope(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        // 1. 优先取方法上的注解
        IgnoreDataScope ignoreDataScope = AnnotationUtil.getAnnotation(method, IgnoreDataScope.class);
        log.info("当前用户：{}是否忽略数据范围筛选:{}", UserContextHolder.getLoginUser().getUsername(),ignoreDataScope!=null);
        return Objects.nonNull(ignoreDataScope);
    }

    /**
     * 通过反射给签名
     * @param result
     */
    private void setCheckSignField(Object result,ProceedingJoinPoint pjp) {
        String className = pjp.getTarget().getClass().getName();
        if(Objects.nonNull(result) && result instanceof Results){
            Results results = (Results)result;
            Object data = results.get("data");
            if(data instanceof BaseResponse){
                Object id = ReflectUtil.getFieldValue(data, "id");
                if(Objects.nonNull(id)){
                    ReflectUtil.setFieldValue(data,"signature",MD5Utils.getMd5(className.concat(id.toString()),Constants.MD5_DEFAULT_SECRET));
                }
            }
            if(data instanceof Collection<?>){
                Collection<?> collection = (Collection<?>) data;
                for (Object item : collection) {
                    if(item instanceof BaseResponse){
                        Object id = ReflectUtil.getFieldValue(item, "id");
                        if(Objects.nonNull(id)){
                            ReflectUtil.setFieldValue(item,"signature",MD5Utils.getMd5(className.concat(id.toString()),Constants.MD5_DEFAULT_SECRET));
                        }
                    }
                }
            }
        }
    }


    /**
     * 校验签名
     * @param pjp
     */
    private void checkSign(ProceedingJoinPoint pjp) {
        String className = pjp.getTarget().getClass().getName();
        Object[] args = pjp.getArgs();
        if(args != null && args.length>0){
            if(args[0] instanceof BaseRequest){
                Object signature = ReflectUtil.getFieldValue(args[0], "signature");
                String checkSign = Objects.isNull(signature)?null:ObjectUtil.toString(signature);
                Object id = ReflectUtil.getFieldValue(args[0], "id");
                if(StrUtil.isNotBlank(checkSign) && Objects.nonNull(id)){
                    String md5 = MD5Utils.getMd5(className.concat(id.toString()),Constants.MD5_DEFAULT_SECRET);
                    if(!checkSign.equals(md5)){
                        throw new SoException(Constants.CHECK_SIGN_MESSAGE);
                    }
                }
            }
        }
    }

    private void filterDataScope(boolean ignoreDataScope) {
        DataScopeEnum dataScopeEnum = UserContextHolder.getLoginUser().getDataScopeEnum();
        if(Objects.isNull(dataScopeEnum)){
            return;
        }
        switch (dataScopeEnum){
            case SELF:
                if(!ignoreDataScope){
                    PermissionThreadLocal.set();
                }
                break;
            case VISITOR:
                this.processVisitorDataScope(ignoreDataScope);
                break;
            default:
                break;
        }

    }

    /**
     * 处理游客数据权限
     */
    private void processVisitorDataScope(boolean ignoreDataScope) {
        //拦截post，put，delete请求不得操作
        String lowerCase = HttpContextUtils.getHttpServletRequest().getMethod().toLowerCase();
        if (Constants.HTTP_POST_METHOD.equals(lowerCase) || Constants.HTTP_PUT_METHOD.equals(lowerCase)
                || Constants.HTTP_DELETE_METHOD.equals(lowerCase)) {
            throw new SoException(HttpStatus.FORBIDDEN.value(), Constants.NO_PERMISSION_MESSAGE);
        }
        if(!ignoreDataScope){
            PermissionThreadLocal.set();
        }
    }

}
