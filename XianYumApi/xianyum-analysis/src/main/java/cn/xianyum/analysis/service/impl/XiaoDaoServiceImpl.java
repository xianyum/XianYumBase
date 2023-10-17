package cn.xianyum.analysis.service.impl;

import cn.xianyum.analysis.dao.XiaoDaoMapper;
import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import cn.xianyum.analysis.entity.response.XiaoDaoResponse;
import cn.xianyum.analysis.service.XiaoDaoService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 实时推送更新消息
     */
    @Override
    public void push() {
        List<XiaoDaoEntity> pushInfo = xiaoDaoMapper.selectList(new QueryWrapper<XiaoDaoEntity>().eq("push_status", 0)
        );

        for (XiaoDaoEntity xiaoDaoEntity : pushInfo) {

            Map<String,Object> content = new LinkedHashMap<>();
            content.put("活动内容：",xiaoDaoEntity.getTitle());
            content.put("活动url：",xiaoDaoEntity.getUrl());

            MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
            messageSenderEntity.setFormUrl(xiaoDaoEntity.getUrl());
            messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
            messageSender.sendAsyncMessage(MessageCodeEnums.NEW_ACTIVITY_NOTIFY.getMessageCode(),messageSenderEntity);

            xiaoDaoEntity.setPushStatus(1);
            xiaoDaoEntity.setPushTime(new Date());
            xiaoDaoMapper.updateById(xiaoDaoEntity);
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

}
