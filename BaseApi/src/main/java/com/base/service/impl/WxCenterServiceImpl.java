package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.utils.StringUtil;
import com.base.dao.WxCenterMapper;
import com.base.entity.po.wx_center.WxCenterEntity;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.WxCenterRequest;
import com.base.service.iservice.WxCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2019/9/29 13:57
 */
@Slf4j
@Service
public class WxCenterServiceImpl implements WxCenterService {

    @Autowired
    private WxCenterMapper wxCenterMapper;

    @Override
    public void save(String json) {
        try {
            if(StringUtil.isNotEmpty(json)){
                String data = JSONObject.parseObject(json).getString("data");
                WxCenterEntity wxCenterEntity = JSONObject.parseObject(data, WxCenterEntity.class);
                wxCenterEntity.setCreateTime(new Date());
                wxCenterMapper.insert(wxCenterEntity);
            }
        }catch (Exception e){
            log.error("回调数据出错,{},{}",json,e.getMessage());
        }
    }

    @Override
    public IPage<WxCenterEntity> queryAll(WxCenterRequest request) {
        Page<WxCenterEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<WxCenterEntity> wxCenterEntityQueryWrapper;
        if(StringUtil.isEmpty(request.getAppName())){
            wxCenterEntityQueryWrapper = new QueryWrapper<WxCenterEntity>().orderByDesc("create_time");
        }else{
            wxCenterEntityQueryWrapper = new QueryWrapper<WxCenterEntity>().like("app_name", request.getAppName()).orderByDesc("create_time");
        }
        IPage<WxCenterEntity> iPage = wxCenterMapper.selectPage(page, wxCenterEntityQueryWrapper);
        iPage.getRecords().stream().forEach(p -> p.setUid(StringUtils.overlay(p.getUid(),"********",5,24)));
        return iPage;
    }
}
