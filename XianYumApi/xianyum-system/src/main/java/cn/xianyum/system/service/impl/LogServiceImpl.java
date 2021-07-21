package cn.xianyum.system.service.impl;


import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.common.utils.PushCenterSystemUtils;
import cn.xianyum.system.dao.LogMapper;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import cn.xianyum.system.service.LogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/6/20 13:52
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private PushCenterSystemUtils pushCenterSystemUtils;


    /**
     * 保存系统日志
     *
     * @param logEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(LogEntity logEntity) {

        logEntity.setId(UUIDUtils.UUIDReplace());
        if(StringUtil.isEmpty(logEntity.getIp())){
            HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
            String ip = IPUtils.getIpAddr(httpServletRequest);
            logEntity.setIp(ip);
            logEntity.setIpInfo(IPUtils.getIpInfo(ip));
        }

        logEntity.setCreateTime(new Date());
        if(StringUtil.isEmpty(logEntity.getMethod())){
            logEntity.setMethod(this.getClass().toString());
        }

        if(StringUtil.isEmpty(logEntity.getUsername())){
            if(null != SecurityUtils.getLoginUser()){
                logEntity.setUsername(SecurityUtils.getLoginUser().getUsername());
            }else{
                logEntity.setUsername("system");
            }
        }
        if(StringUtil.isEmpty(logEntity.getOperation())){
            logEntity.setOperation("系统无法识别用户操作");
        }

        if(StringUtil.isEmpty(logEntity.getParams())){
            logEntity.setParams("{}");
        }

        if(null == logEntity.getTime()){
            logEntity.setTime(0L);
        }
        logMapper.insert(logEntity);
    }

    @Override
    public IPage<LogEntity> queryAll(LogRequest request) {
        Page<LogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        //查询总记录数
        page.setSearchCount(true);
        if(SecurityUtils.getLoginUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            request.setUsername(SecurityUtils.getLoginUser().getUsername());
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

        Map<String,Object> content = new LinkedHashMap<>();
        content.put("今日实时量：",nowCount);
        content.put("昨日访问量：",oneCount);
        content.put("昨日环比量：",hoopeCountStr);
        content.put("统计时间：",now.toString(DateUtils.DATE_TIME_PATTERN));
        pushCenterSystemUtils.push(content,"Base-Api接口统计报表推送");

    }

    @Override
    public List<LogResponse> getVisitCountCharts(LogRequest request) {
        if(SecurityUtils.getLoginUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            request.setUsername(SecurityUtils.getLoginUser().getUsername());
        }else{
            request.setUsername(null);
        }
        List<LogResponse> responses = logMapper.getVisitCountCharts(request);
        return responses;
    }
}
