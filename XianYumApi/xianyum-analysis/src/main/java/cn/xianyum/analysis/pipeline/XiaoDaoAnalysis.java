package cn.xianyum.analysis.pipeline;

import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Request;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;
import java.util.List;

/**
 * @author zhangwei
 * @date 2019/9/25 15:17
 */
@Data
@Gecco(matchUrl = "https://www.x6g.com/", pipelines={"xiaoDaoArticleLine"},timeout = 300000,downloader="htmlUnitDownloder")
public class XiaoDaoAnalysis implements HtmlBean {

    private static final long serialVersionUID = 1225018257932399804L;

    @Request
    private HttpRequest request;

    @HtmlField(cssPath = "#newslist > ul:nth-child(1) > li:nth-child(n)")
    private List<XiaoDaoArticle> xiaoDaoArticles;

}
