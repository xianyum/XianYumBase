package cn.xianyum.analysis.infra.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import cn.xianyum.analysis.infra.common.utils.HttpGetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/9/25 15:35
 */
@JobHandler("xiaoDaoAnalysisTask")
public class XiaoDaoAnalysisTask implements IJobHandler {

    @Autowired
    private PipelineFactory springPipelineFactory;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        HttpGetRequest startUrl = new HttpGetRequest("https://www.x6g.com/");
        startUrl.setCharset("UTF-8");
        GeccoEngine.create()
                //Gecco搜索的包路径
                .classpath("cn.xianyum.analysis")
                .pipelineFactory(springPipelineFactory)
                //开始抓取的页面地址
                .start(startUrl)
                //开启几个爬虫线程
                .thread(1)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .run();
        return ReturnT.SUCCESS;
    }
}
