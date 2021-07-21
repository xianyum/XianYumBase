package cn.xianyum.analysis.pipeline;

import com.geccocrawler.gecco.annotation.Href;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Text;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2019/9/25 15:07
 */
@Data
public class XiaoDaoArticle implements HtmlBean {

    @Text
    @HtmlField(cssPath = "span")
    private String time;

    @Text
    @HtmlField(cssPath = "a")
    private String title;

    @Href(value = "href")
    @HtmlField(cssPath = "a")
    private String url;
}
