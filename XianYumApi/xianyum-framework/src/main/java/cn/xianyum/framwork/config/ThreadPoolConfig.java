package cn.xianyum.framwork.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync
@ComponentScan("cn.xianyum")
public class ThreadPoolConfig {

    @Value("${xian_yum.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${xian_yum.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${xian_yum.executor.queue-capacity}")
    private int queueCapacity;

    @Value("${xian_yum.executor.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Bean(name = {"xianYumTaskExecutor"})
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("thread-xianyum-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}
