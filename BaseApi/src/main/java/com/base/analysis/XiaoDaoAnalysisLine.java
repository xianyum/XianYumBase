package com.base.analysis;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.base.common.utils.BeanUtils;
import com.base.common.utils.DateUtils;
import com.base.common.utils.StringUtil;
import com.base.common.utils.UUIDUtils;
import com.base.dao.XiaoDaoMapper;
import com.base.entity.po.UserEntity;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
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
    XiaoDaoMapper xiaoDaoMapper;

    @Override
    public void process(XiaoDaoAnalysis xiaoDaoAnalysis) {
        try {
            List<XiaoDaoArticle> xiaoDaoArticles = xiaoDaoAnalysis.getXiaoDaoArticles();
            List<XiaoDaoArticle> collect = xiaoDaoArticles.stream().filter(p -> DateUtils.format(new Date(), "MM-dd").equals(p.getTime().trim())).collect(Collectors.toList());
            if (collect != null && collect.size() > 0) {
                for (XiaoDaoArticle item : collect) {
                    Integer count = xiaoDaoMapper.selectCount(
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
                    bean.setParams(JSONObject.toJSONString(xiaoDaoAnalysis.getRequest()));
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
