package com.base.task;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.request.HttpGetRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangwei
 * @date 2019/9/25 15:35
 */
@Component
@Slf4j
public class XiaoDaoAnalysisTask {
    @Resource
    private PipelineFactory springPipelineFactory;

    @Scheduled(cron = "0/10 * * * * ?")  //每隔5分钟执行一次
    public void reportDataJob() {
        HttpGetRequest startUrl = new HttpGetRequest("https://www.xd0.com/");
        startUrl.setCharset("UTF-8");
        GeccoEngine.create()
                //Gecco搜索的包路径
                .classpath("com.base.analysis")
                .pipelineFactory(springPipelineFactory)
                //开始抓取的页面地址
                .start(startUrl)
                //开启几个爬虫线程
                .thread(1)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(2000)
                .run();
    }

}
