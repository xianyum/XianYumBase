package cn.xianyum.common.annotation;

import java.lang.annotation.*;

/**
 * 忽略数据权限注解
 * @author zhangwei
 * @date 2020/11/5 21:14
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface IgnoreDataScope {

}
