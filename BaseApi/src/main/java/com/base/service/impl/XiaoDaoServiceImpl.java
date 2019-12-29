package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.utils.HttpUtils;
import com.base.common.utils.StringUtil;
import com.base.dao.WxCenterMapper;
import com.base.dao.XiaoDaoMapper;
import com.base.entity.po.wx_center.WxCenterEntity;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.XiaoDaoRequest;
import com.base.service.iservice.XiaoDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
@Slf4j
@Service
public class XiaoDaoServiceImpl extends ServiceImpl<XiaoDaoMapper, XiaoDaoEntity>  implements XiaoDaoService {

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    @Autowired
    private WxCenterMapper wxCenterMapper;

    private static final String URL = "http://wxpusher.zjiecode.com/api/send/message";

    private static final String APP_TOKEN = "AT_gEUq49NQBwiX29wvaNNawCRdyoBMJ8RA";

    private static final String SUFFIX = "【xianyum.cn】推送：";

    private static final String APP_KEY = "AK_l8hjWUB0dlmkmBwc";

    /**
     * 实时推送更新消息
     */
    @Override
    public void push() {
        List<XiaoDaoEntity> pushInfo = xiaoDaoMapper.selectList(new QueryWrapper<XiaoDaoEntity>().eq("push_status", 0)
        );
        List<WxCenterEntity> wxCenterEntities = wxCenterMapper.selectList(new QueryWrapper<WxCenterEntity>().eq("app_key", APP_KEY));
        if(pushInfo != null && wxCenterEntities != null && pushInfo.size() >0 && wxCenterEntities.size() >0){
            for (XiaoDaoEntity xiaoDaoEntity : pushInfo) {
                JSONObject params = getParams(xiaoDaoEntity, wxCenterEntities);
                HttpUtils.sendPostJson(URL, params.toString());
                xiaoDaoEntity.setPushStatus(1);
                xiaoDaoEntity.setPushTime(new Date());
                xiaoDaoMapper.updateById(xiaoDaoEntity);
            }
        }
    }

    @Override
    public IPage<XiaoDaoEntity> queryAll(XiaoDaoRequest request) {
        Page<XiaoDaoEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<XiaoDaoEntity> xiaoDaoEntityQueryWrapper  = new QueryWrapper<XiaoDaoEntity>()
                .like(StringUtil.isEmpty(request.getTitle()),"title", request.getTitle()).orderByDesc("create_time");
        IPage<XiaoDaoEntity> iPage = xiaoDaoMapper.selectPage(page, xiaoDaoEntityQueryWrapper);
        return iPage;
    }

    /**
     * 构建推送微信数据
     * @param xiaoDaoEntity
     * @param wxCenterEntitys
     * @return
     */
    public JSONObject getParams(XiaoDaoEntity xiaoDaoEntity,List<WxCenterEntity> wxCenterEntitys){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appToken",APP_TOKEN);
        jsonObject.put("contentType",1);//内容类型 1表示文字  2表示html 3表示markdown
        jsonObject.put("url",xiaoDaoEntity.getUrl());
        jsonObject.put("uids",wxCenterEntitys.stream().map(p -> p.getUid()).collect(Collectors.toList()));
        jsonObject.put("content",SUFFIX+xiaoDaoEntity.getTitle());
        return jsonObject;
    }
}
