package cn.xianyum.framwork.security.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/11/3 14:31
 */

/**
 * 1. SpelExpressionParser 在进行bean解析时 需要使用到 StandardEvaluationContext
 *    否则无法正确的解析bean  会报错：EL1057E: No bean resolver registered in the context to resolve access to bean 'qcAuth'
 * 2. StandardEvaluationContext 可以为SpelExpressionParser 提供被解析的Bean
 * 3. 所以在这里手动的注入一个 包含了ApplicationContext 的 StandardEvaluationContext
 *    用于后续使用 SpelExpressionParser
 */
@Component
public class PermissionStandardEvaluationContext extends StandardEvaluationContext implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //将application Context 提供给 StandardEvaluationContext
        setBeanResolver(new BeanFactoryResolver(applicationContext));
    }
}
