package cn.xianyum.common.annotation;

import cn.xianyum.common.enums.OperTypeEnum;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 作用在Controller方法上，自动记录操作日志
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    OperTypeEnum operType() default OperTypeEnum.OTHER;

    /**
     * 操作描述
     */
    String desc() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;

    /**
     * 是否保存返回结果
     */
    boolean saveResult() default false;

    /**
     * 排除的敏感字段，支持数组传入，如 {"password","token"}
     */
    String[] excludeFields() default {"password"};
}
