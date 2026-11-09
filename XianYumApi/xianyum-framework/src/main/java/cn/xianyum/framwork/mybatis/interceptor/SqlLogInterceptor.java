package cn.xianyum.framwork.mybatis.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description SQL日志拦截器，打印参数已拼接完成的完整SQL。
 * 拦截StatementHandler而非Executor，保证打印的是经过MyBatis-Plus数据权限、分页等InnerInterceptor改写后真正执行的SQL
 * @Author ZhangWei
 * @Date 2026/11/09
 */
@Slf4j(topic = "cn.xianyum.sql")
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlLogInterceptor implements Interceptor {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 需要屏蔽打印的Mapper方法全名，支持 * 通配符
     */
    private List<String> excludeStatements = List.of();

    public void setExcludeStatements(List<String> excludeStatements) {
        this.excludeStatements = excludeStatements;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MappedStatement mappedStatement = getMappedStatement(statementHandler);
        if (mappedStatement == null || isExcluded(mappedStatement.getId())) {
            return invocation.proceed();
        }
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = buildCompleteSql(boundSql, mappedStatement.getConfiguration());

        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.info("[SQL] {} | {}ms | {}", mappedStatement.getId(), cost, sql);
        }
    }

    /**
     * 当前StatementHandler可能已被MyBatis-Plus等插件代理，需要先解包再取MappedStatement
     */
    private MappedStatement getMappedStatement(StatementHandler statementHandler) {
        Object target = statementHandler;
        while (Proxy.isProxyClass(target.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(target);
            if (!(handler instanceof Plugin)) {
                break;
            }
            target = SystemMetaObject.forObject(handler).getValue("target");
        }
        return (MappedStatement) SystemMetaObject.forObject(target).getValue("delegate.mappedStatement");
    }

    private boolean isExcluded(String statementId) {
        for (String pattern : excludeStatements) {
            if (!StringUtils.hasText(pattern)) {
                continue;
            }
            String trimmed = pattern.trim();
            if (!trimmed.contains("*")) {
                if (trimmed.equals(statementId)) {
                    return true;
                }
                continue;
            }
            String regex = trimmed.replace(".", "\\.").replace("$", "\\$").replace("*", ".*");
            if (statementId.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将SQL中的占位符替换为真实参数值
     */
    private String buildCompleteSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return sql;
        }
        Object parameterObject = boundSql.getParameterObject();
        StringBuilder completeSql = new StringBuilder(sql.length() + 64);
        int parameterIndex = 0;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '?' && parameterIndex < parameterMappings.size()) {
                ParameterMapping parameterMapping = parameterMappings.get(parameterIndex++);
                Object value = getParameterValue(boundSql, parameterMapping.getProperty(), parameterObject, configuration);
                completeSql.append(formatValue(value));
            } else {
                completeSql.append(c);
            }
        }
        return completeSql.toString();
    }

    private Object getParameterValue(BoundSql boundSql, String property, Object parameterObject, Configuration configuration) {
        if (boundSql.hasAdditionalParameter(property)) {
            return boundSql.getAdditionalParameter(property);
        }
        if (parameterObject == null) {
            return null;
        }
        if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
            return parameterObject;
        }
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        if (metaObject.hasGetter(property)) {
            return metaObject.getValue(property);
        }
        return null;
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Date) {
            return "'" + new SimpleDateFormat(DATE_FORMAT).format((Date) value) + "'";
        }
        if (value instanceof byte[]) {
            StringBuilder hex = new StringBuilder("0x");
            for (byte b : (byte[]) value) {
                hex.append(String.format("%02X", b));
            }
            return hex.toString();
        }
        return "'" + value.toString().replace("'", "''") + "'";
    }
}
