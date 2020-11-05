package com.base.common.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限注解
 * 只有管理员才可访问
 * @author zhangwei
 * @date 2020/11/5 21:14
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface AdminPermission {
}
