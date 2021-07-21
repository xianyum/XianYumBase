package cn.xianyum.common.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 确保应用退出时能关闭后台线程
 * @author zhangwei
 * @date 2019/12/10 10:41
 */
@Component
public class ShutdownManager {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownManager.class);

    @PreDestroy
    public void destroy()
    {
        shutdownAsyncManager();
    }

    /**
     * 停止异步执行任务
     */
    private void shutdownAsyncManager()
    {
        try
        {
            AsyncManager.async().shutdown();
            logger.info("task thread pool closed");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
