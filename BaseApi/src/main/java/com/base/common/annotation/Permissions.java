package com.base.common.annotation;

import java.lang.annotation.*;

/**
 * 此注解禁止操作
 * @author zhangwei
 * @date 2020/11/5 21:14
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Permissions {

    // 默认只校验游客身份
    String[] value() default {"visitor"};
}
