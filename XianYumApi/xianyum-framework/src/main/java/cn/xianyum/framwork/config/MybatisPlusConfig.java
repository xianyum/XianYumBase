package cn.xianyum.framwork.config;


import cn.xianyum.framwork.mybatis.handler.RoleDataPermissionHandler;
import cn.xianyum.framwork.mybatis.interceptor.MybatisPlusPermissionInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
public class MybatisPlusConfig {

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
}
