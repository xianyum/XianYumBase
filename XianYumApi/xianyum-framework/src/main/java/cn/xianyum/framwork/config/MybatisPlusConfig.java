package cn.xianyum.framwork.config;


import cn.hutool.core.util.StrUtil;
import cn.xianyum.framwork.mybatis.handler.RoleDataPermissionHandler;
import cn.xianyum.framwork.mybatis.interceptor.MybatisPlusPermissionInterceptor;
import cn.xianyum.framwork.mybatis.interceptor.SqlLogInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
public class MybatisPlusConfig {

	/**
	 * 不打印SQL日志的Mapper方法全名，多个用逗号分隔，支持 * 通配符
	 */
	@Value("${mybatis-plus.sql-log.exclude:}")
	private String sqlLogExcludes;

	/**
	 * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		MybatisPlusPermissionInterceptor dataPermissionInterceptor = new MybatisPlusPermissionInterceptor(new RoleDataPermissionHandler());
		interceptor.addInnerInterceptor(dataPermissionInterceptor);
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}

	/**
	 * SQL日志拦截器：打印参数已拼接完成的完整SQL
	 */
	@Bean
	public SqlLogInterceptor sqlLogInterceptor() {
		SqlLogInterceptor interceptor = new SqlLogInterceptor();
		interceptor.setExcludeStatements(Arrays.asList(sqlLogExcludes.split(StrUtil.COMMA)));
		return interceptor;
	}
}
