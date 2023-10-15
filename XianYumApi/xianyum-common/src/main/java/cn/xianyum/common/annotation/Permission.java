package cn.xianyum.common.annotation;

import cn.xianyum.common.enums.PermissionStrategy;
import java.lang.annotation.*;

/**
 * 权限注解
 * @author zhangwei
 * @date 2020/11/5 21:14
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Permission {

    /** 用户权限校验策略 */
    PermissionStrategy strategy() default PermissionStrategy.ALLOW_ALL;

    /** 是否公开接口 */
    boolean publicApi() default false;

}