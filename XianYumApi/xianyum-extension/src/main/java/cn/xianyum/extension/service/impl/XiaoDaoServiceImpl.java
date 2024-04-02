package cn.xianyum.extension.service.impl;

import cn.xianyum.extension.dao.XiaoDaoMapper;
import cn.xianyum.extension.entity.po.XiaoDaoAnalysisEntity;
import cn.xianyum.extension.entity.po.XiaoDaoEntity;
import cn.xianyum.extension.entity.request.XiaoDaoRequest;
import cn.xianyum.extension.entity.response.XiaoDaoResponse;
import cn.xianyum.extension.service.XiaoDaoService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
@Slf4j
@Service
public class XiaoDaoServiceImpl implements XiaoDaoService {

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    @Autowired
    private MessageSender messageSender;

    public static final String XIAO_DAO_URL = "https://x6d.com";

    public static final String FILTER_NAME = "广告";

    /**
     * 实时推送更新消息
     */
    @Override
    public void push(List<XiaoDaoEntity> pushInfo) {
        for (XiaoDaoEntity xiaoDaoEntity : pushInfo) {
            Map<String,Object> content = new LinkedHashMap<>();
            content.put("活动内容：",xiaoDaoEntity.getTitle());
            content.put("活动url：",xiaoDaoEntity.getUrl());
            MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
            messageSenderEntity.setFormUrl(xiaoDaoEntity.getUrl());
            messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
            messageSender.sendAsyncMessage(MessageCodeEnums.NEW_ACTIVITY_NOTIFY.getMessageCode(),messageSenderEntity);
        }

    }

    @Override
    public PageResponse<XiaoDaoResponse> getPage(XiaoDaoRequest request) {
        Page<XiaoDaoEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<XiaoDaoEntity> xiaoDaoEntityQueryWrapper  = new QueryWrapper<XiaoDaoEntity>()
                .like(StringUtil.isNotEmpty(request.getTitle()),"title", request.getTitle()).orderByDesc("create_time");
        IPage<XiaoDaoEntity> xiaoDaoEntityPage = xiaoDaoMapper.selectPage(page, xiaoDaoEntityQueryWrapper);
        return PageResponse.of(xiaoDaoEntityPage,XiaoDaoResponse.class);
    }

    /**
     * 解析爬虫数据
     * @param xiaoDaoAnalysisEntity
     */
    @Override
    public void xxlCrawlerParse(XiaoDaoAnalysisEntity xiaoDaoAnalysisEntity) {
        if(Objects.isNull(xiaoDaoAnalysisEntity)){
            return;
        }
        log.info("爬虫小刀娱乐网数据：{}", JSONObject.toJSONString(xiaoDaoAnalysisEntity));
        List<XiaoDaoEntity> xiaoDaoEntityList = new ArrayList<>();
        for (int i = 0; i < xiaoDaoAnalysisEntity.getTime().size(); i++) {
            List<String> times = xiaoDaoAnalysisEntity.getTime();
            String time = times.get(i);
            String url = XIAO_DAO_URL+xiaoDaoAnalysisEntity.getUrl().get(i);
            String title = xiaoDaoAnalysisEntity.getTitle().get(i);
            if(time.contains(FILTER_NAME)){
               continue;
            }
            LambdaQueryWrapper<XiaoDaoEntity> queryWrapper = Wrappers.<XiaoDaoEntity>lambdaQuery()
                    .eq(XiaoDaoEntity::getUrl,url);
            Long count = xiaoDaoMapper.selectCount(queryWrapper);
            if(count > 0){
                continue;
            }
            XiaoDaoEntity bean = new XiaoDaoEntity();
            bean.setId(UUIDUtils.UUIDReplace());
            bean.setUrl(url);
            bean.setTitle(title);
            int insert = xiaoDaoMapper.insert(bean);
            if(insert > 0){
                xiaoDaoEntityList.add(bean);
            }
        }
        this.push(xiaoDaoEntityList);
    }

}
