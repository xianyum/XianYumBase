package cn.xianyum.common.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * @author zhangwei
 * @date 2020/11/5 21:14
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Permission {


    /** 权限授权 true:校验通过 false：校验不通过*/
    String value() default "true";

    /** 是否公开接口 */
    boolean publicApi() default false;

    /** 是否忽略数据权限 true:忽略  false：不忽略 */
    boolean ignoreDataScope() default false;

}
