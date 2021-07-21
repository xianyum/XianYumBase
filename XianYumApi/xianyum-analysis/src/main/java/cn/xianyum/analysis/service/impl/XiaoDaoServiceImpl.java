package cn.xianyum.analysis.service.impl;

import cn.xianyum.analysis.dao.XiaoDaoMapper;
import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import cn.xianyum.analysis.service.XiaoDaoService;
import cn.xianyum.analysis.utils.PushAnalysisUtils;
import cn.xianyum.common.utils.StringUtil;
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
    private PushAnalysisUtils pushAnalysisUtils;

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
            String pushId = pushAnalysisUtils.push(content,"最新活动通知");

            xiaoDaoEntity.setPushStatus(1);
            xiaoDaoEntity.setPushTime(new Date());
            xiaoDaoEntity.setPushId(pushId);
            xiaoDaoMapper.updateById(xiaoDaoEntity);
        }

    }

    @Override
    public IPage<XiaoDaoEntity> queryAll(XiaoDaoRequest request) {
        Page<XiaoDaoEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<XiaoDaoEntity> xiaoDaoEntityQueryWrapper  = new QueryWrapper<XiaoDaoEntity>()
                .like(StringUtil.isNotEmpty(request.getTitle()),"title", request.getTitle()).orderByDesc("create_time");
        IPage<XiaoDaoEntity> iPage = xiaoDaoMapper.selectPage(page, xiaoDaoEntityQueryWrapper);
        return iPage;
    }

}
