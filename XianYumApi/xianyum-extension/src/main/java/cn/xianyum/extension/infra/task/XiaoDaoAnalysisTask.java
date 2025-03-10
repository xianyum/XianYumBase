package cn.xianyum.extension.infra.task;

import cn.xianyum.extension.entity.po.XiaoDaoAnalysisEntity;
import cn.xianyum.extension.service.XiaoDaoService;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.loader.strategy.JsoupPageLoader;
import com.xuxueli.crawler.parser.PageParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/9/25 15:35
 */
@JobHandler("xiaoDaoAnalysisTask")
@Slf4j
public class XiaoDaoAnalysisTask implements IJobHandler {

    @Autowired
    private XiaoDaoService xiaoDaoService;

    public static final String XIAO_DAO_URL = "https://x6d.com/";

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie","Hm_lvt_d60e542115f2ca02adf147d409bb5f6b=1711338582,1711357263,1711520956; Hm_lpvt_d60e542115f2ca02adf147d409bb5f6b=1711521412");
        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headerMap.put("Sec-Ch-Ua","\"Google Chrome\";v=\"123\", \"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"123\"");
        headerMap.put("Sec-Ch-Ua-Mobile","?0");
        headerMap.put("Sec-Ch-Ua-Platform","\"Windows\"");
        headerMap.put("Sec-Fetch-Dest","document");
        headerMap.put("Sec-Fetch-Mode","navigate");
        headerMap.put("Sec-Fetch-Site","none");
        headerMap.put("Sec-Fetch-User","?1");
        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls(XIAO_DAO_URL)
                .setAllowSpread(false)
                .setThreadCount(1)
                .setHeaderMap(headerMap)
                .setPageLoader(new JsoupPageLoader())
                .setPageParser(new PageParser<XiaoDaoAnalysisEntity>() {
                    @Override
                    public void parse(Document document, Element element, XiaoDaoAnalysisEntity xiaoDaoAnalysisEntity) {
                        xiaoDaoService.xxlCrawlerParse(xiaoDaoAnalysisEntity);
                    }
                })
                .build();
        crawler.start(false);
        return ReturnT.SUCCESS;
    }
}
