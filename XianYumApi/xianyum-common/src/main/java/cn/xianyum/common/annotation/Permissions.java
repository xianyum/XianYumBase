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
public @interface Permissions {

    /** 权限校验策略 */
    PermissionStrategy strategy() default PermissionStrategy.ALLOW_ALL;
}
