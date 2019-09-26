package com.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.utils.HttpUtils;
import com.base.common.utils.StringUtil;
import com.base.dao.XiaoDaoMapper;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.XiaoDaoRequest;
import com.base.service.iservice.XiaoDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
@Slf4j
@Service
public class XiaoDaoServiceImpl extends ServiceImpl<XiaoDaoMapper, XiaoDaoEntity>  implements XiaoDaoService {

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    private final String url = "https://sc.ftqq.com/SCU27598Tb8a820a6e0535e0eb2c0ad0c496a059c5b2492ff0a135.send";
    /**
     * 实时推送更新消息
     */
    @Override
    public void push() {
        List<XiaoDaoEntity> pushInfo = xiaoDaoMapper.selectList(new QueryWrapper<XiaoDaoEntity>().eq("push_status", 0)
        );
        if(pushInfo != null && pushInfo.size() >0 ){
            for (XiaoDaoEntity xiaoDaoEntity : pushInfo) {
                String text = xiaoDaoEntity.getTitle();
                String desp = "来自BaseApi【47.103.20.104】服务实时推送。[访问网页]("+xiaoDaoEntity.getUrl()+")";
                HttpUtils.sendGet(url,"text="+text+"&desp="+desp);
                xiaoDaoEntity.setPushStatus(1);
                xiaoDaoEntity.setPushTime(new Date());
                xiaoDaoMapper.updateById(xiaoDaoEntity);
            }
        }
    }

    @Override
    public IPage<XiaoDaoEntity> queryAll(XiaoDaoRequest request) {
        Page<XiaoDaoEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<XiaoDaoEntity> xiaoDaoEntityQueryWrapper = null;
        if(StringUtil.isEmpty(request.getTitle())){
            xiaoDaoEntityQueryWrapper = new QueryWrapper<XiaoDaoEntity>().orderByDesc("create_time");
        }else{
            xiaoDaoEntityQueryWrapper = new QueryWrapper<XiaoDaoEntity>().like("title", request.getTitle()).orderByDesc("create_time");
        }
        IPage<XiaoDaoEntity> iPage = xiaoDaoMapper.selectPage(page, xiaoDaoEntityQueryWrapper);
        return iPage;
    }
}
