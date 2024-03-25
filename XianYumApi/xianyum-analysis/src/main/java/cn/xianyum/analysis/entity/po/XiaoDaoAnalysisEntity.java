package cn.xianyum.analysis.entity.po;


import com.xuxueli.crawler.annotation.PageFieldSelect;
import com.xuxueli.crawler.annotation.PageSelect;
import com.xuxueli.crawler.conf.XxlCrawlerConf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2024/3/25 11:45
 */
@PageSelect(cssQuery  = "body")
@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class XiaoDaoAnalysisEntity {


    @PageFieldSelect(cssQuery = "#newslist > ul:nth-child(1) > li:nth-child(n) > span")
    private List<String> time;

    @PageFieldSelect(cssQuery = "#newslist > ul:nth-child(1) > li:nth-child(n) > a")
    private List<String> title;

    @PageFieldSelect(cssQuery = "#newslist > ul:nth-child(1) > li:nth-child(n) > a ",selectType = XxlCrawlerConf.SelectType.ATTR,selectVal = "href")
    private List<String> url;
}
