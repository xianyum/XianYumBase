package cn.xianyum.analysis.infra.pipeline;

import com.alibaba.fastjson2.JSON;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.spider.SpiderBean;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2019/9/25 17:27
 */
@Service("consolePipeline")
public class ConsolePipeline implements Pipeline<SpiderBean> {
    @Override
    public void process(SpiderBean spiderBean) {
        System.out.println(JSON.toJSONString(spiderBean));
    }
}