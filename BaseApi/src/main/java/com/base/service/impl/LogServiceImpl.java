package com.base.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.exception.SoException;
import com.base.common.utils.AuthUserToken;
import com.base.common.utils.DateUtils;
import com.base.common.utils.DingDingPushUtils;
import com.base.common.utils.HttpUtils;
import com.base.dao.LogMapper;
import com.base.dao.WxCenterMapper;
import com.base.entity.enums.PermissionEnum;
import com.base.entity.po.LogEntity;
import com.base.entity.po.wx_center.WxCenterEntity;
import com.base.entity.request.LogRequest;
import com.base.entity.response.LogResponse;
import com.base.service.iservice.LogService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2019/6/20 13:52
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements LogService {

    @Value("${push.wxpusher.url}")
    private String URL;

    @Value("${push.wxpusher.app_token}")
    private String APP_TOKEN;

    @Value("${push.wxpusher.app_key}")
    private String APP_KEY;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private WxCenterMapper wxCenterMapper;

    /**
     * 保存系统日志
     *
     * @param logEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(LogEntity logEntity) {
        logMapper.insert(logEntity);
    }

    @Override
    public IPage<LogEntity> queryAll(LogRequest request) {
        Page<LogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        //查询总记录数
        page.setSearchCount(true);
        if(AuthUserToken.getUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            request.setUsername(AuthUserToken.getUser().getUsername());
        }else{
            request.setUsername(null);
        }
        List<LogEntity> list = logMapper.queryAll(request, page);
        page.setRecords(list);
        return page;
    }

    @Override
    public void deleteById(String[] logIdS) {
        if(logIdS == null || logIdS.length <= 0){
            throw new SoException("非法传参！");
        }
        for(String id : logIdS){
            logMapper.deleteById(id);
        }
    }

    @Override
    public void push() {
        DateTime now = DateTime.now();
        String nowDay = now.toString("yyyy-MM-dd");
        String oneDay = now.minusDays(1).toString("yyyy-MM-dd");
        String twoDay = now.minusDays(2).toString("yyyy-MM-dd");

        LogRequest logRequest = new LogRequest();
        logRequest.setQueryTime(nowDay);
        Integer nowCount = logMapper.getCount(logRequest);

        logRequest.setQueryTime(oneDay);
        Integer oneCount = logMapper.getCount(logRequest);

        logRequest.setQueryTime(twoDay);
        Integer twoCount = logMapper.getCount(logRequest);
        Integer hoopeCount = oneCount - twoCount;
        String hoopeCountStr = hoopeCount.toString();
        if(hoopeCount > 0){
            hoopeCountStr = "+"+hoopeCount;
        }

        StringBuilder markdownStr = new StringBuilder();
        markdownStr.append("#### Base-Api接口统计报表推送");
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("- 今日实时量：");
        markdownStr.append(nowCount);
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("- 昨日访问量：");
        markdownStr.append(oneCount);
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("- 昨日环比量：");
        markdownStr.append(hoopeCountStr);
        markdownStr.append("\n");
        markdownStr.append(">");
        markdownStr.append("- 统计时间：");
        markdownStr.append(now.toString(DateUtils.DATE_TIME_PATTERN));
        DingDingPushUtils.push("Base-Api接口统计报表推送",markdownStr.toString());

        List<WxCenterEntity> wxCenterEntities = wxCenterMapper.selectList(new QueryWrapper<WxCenterEntity>().eq("app_key", APP_KEY));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appToken",APP_TOKEN);
        jsonObject.put("contentType",3);//内容类型 1表示文字  2表示html 3表示markdown
        jsonObject.put("uids",wxCenterEntities.stream().map(p -> p.getUid()).collect(Collectors.toList()));
        jsonObject.put("content",markdownStr.toString());
        HttpUtils.sendPost(URL, jsonObject);
    }

    @Override
    public List<LogResponse> getVisitCountCharts(LogRequest request) {
        if(AuthUserToken.getUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            request.setUsername(AuthUserToken.getUser().getUsername());
        }else{
            request.setUsername(null);
        }
        List<LogResponse> responses = logMapper.getVisitCountCharts(request);
        return responses;
    }
}
