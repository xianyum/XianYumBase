package cn.xianyum.common.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * @author zhangwei
 * @date 2019/4/18 14:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";
}
