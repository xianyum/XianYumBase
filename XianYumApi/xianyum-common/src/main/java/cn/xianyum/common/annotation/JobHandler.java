package cn.xianyum.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhangwei
 * @date 2022/5/12 22:48
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface JobHandler {

    String value();
}
