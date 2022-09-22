package cn.xianyum.analysis.pipeline;


import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.dao.XiaoDaoMapper;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 爬取小刀页面
 *
 * @author zhangwei
 * @date 2019/9/25 15:52
 */
@PipelineName("xiaoDaoArticleLine")
@Service("xiaoDaoArticleLine")
@Slf4j
public class XiaoDaoAnalysisLine implements Pipeline<XiaoDaoAnalysis> {

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    @Override
    public void process(XiaoDaoAnalysis xiaoDaoAnalysis) {
        try {
            List<XiaoDaoArticle> xiaoDaoArticles = xiaoDaoAnalysis.getXiaoDaoArticles();
            List<XiaoDaoArticle> collect = xiaoDaoArticles.stream().filter(p -> DateUtils.format(new Date(), "MM-dd").equals(p.getTime().trim())).collect(Collectors.toList());
            if (collect != null && collect.size() > 0) {
                for (XiaoDaoArticle item : collect) {
                    Long count = xiaoDaoMapper.selectCount(
                            new QueryWrapper<XiaoDaoEntity>()
                                    .eq(StringUtil.isNotEmpty(item.getTitle()),"title",item.getTitle())
                                    .eq("url",item.getUrl())
                                    .eq("time",DateUtils.format(new Date(), "MM-dd"))
                    );
                    if(count > 0){
                        continue;
                    }
                    if(StringUtil.isEmpty(item.getTitle())){
                        item.setTitle("特别关注："+DateUtils.format(new Date(),"yyyyMMddHHmmss"));
                    }
                    XiaoDaoEntity bean = BeanUtils.copy(item,XiaoDaoEntity.class);
                    bean.setId(UUIDUtils.UUIDReplace());
                    bean.setCreateTime(new Date());
                    bean.setPushStatus(0);
                    xiaoDaoMapper.insert(bean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
