package com.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.base.dao.XiaoDaoMapper;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.service.iservice.XiaoDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
@Slf4j
@Service
public class XiaoDaoServiceImpl implements XiaoDaoService {

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    private final String sckey = "SCU27598Tb8a820a6e0535e0eb2c0ad0c496a059c5b2492ff0a135";

    private final String url = "https://sc.ftqq.com/SCU27598Tb8a820a6e0535e0eb2c0ad0c496a059c5b2492ff0a135.send";
    /**
     * 实时推送更新消息
     */
    @Override
    public void push() {
        List<XiaoDaoEntity> pushInfo = xiaoDaoMapper.selectList(new QueryWrapper<XiaoDaoEntity>().eq("push_status", 0)
        );
        if(pushInfo != null && pushInfo.size() >0 ){

        }
    }
}
