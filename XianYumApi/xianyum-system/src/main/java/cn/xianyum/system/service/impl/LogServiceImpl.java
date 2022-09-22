package cn.xianyum.system.service.impl;


import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.system.dao.LogMapper;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import cn.xianyum.system.service.LogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zhangwei
 * @date 2019/6/20 13:52
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.log.count:logCount}")
    private String logCountPrefix;

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

        Integer oneCount = this.getLogCountWithCache(oneDay);

        Integer twoCount = this.getLogCountWithCache(twoDay);

        Integer hoopCount = oneCount - twoCount;
        String hoopCountStr = hoopCount.toString();
        if(hoopCount > 0){
            hoopCountStr = "+"+hoopCount;
        }

        Map<String,Object> content = new LinkedHashMap<>();
        content.put("今日实时量：",nowCount);
        content.put("昨日访问量：",oneCount);
        content.put("昨日环比量：",hoopCountStr);
        content.put("统计时间：",now.toString(DateUtils.DATE_TIME_PATTERN));

        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setTitle("Base-Api接口统计报表推送");
        messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
        messageSender.sendAsyncMessage(MessageCodeEnums.SYSTEM_VISITS_NOTIFY.getMessageCode(),messageSenderEntity);
    }

    @Override
    public List<LogResponse> getVisitCountCharts(LogRequest request) {

        if(SecurityUtils.getLoginUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            return null;
        }
        String endTime = request.getEndTime();
        Date date = new Date();
        if(StringUtil.isNotEmpty(endTime)){
            date = DateUtils.stringToDate(endTime);
        }

        List<String> dateStrings = DateUtils.minusDate(date, DateUtils.DATE_PATTERN, 15);
        List<LogResponse> responses = new ArrayList<>();
        dateStrings.forEach(p -> {
            LogResponse logResponse = new LogResponse();
            logResponse.setTime(p);
            logResponse.setVisitCount(this.getLogCountWithCache(p));
            responses.add(logResponse);
        });
//        String queryTimeStr = new DateTime(DateUtils.stringToDate(request.getEndTime())).minusDays(15).toString(DateUtils.START_DATE_PATTERN);
//        request.setQueryTime(queryTimeStr);
//        List<LogResponse> responses = logMapper.getVisitCountCharts(request);
        return responses;
    }

    @Override
    public int getLogCountWithCache(String time) {
        String redisKey = logCountPrefix + time;
        int count = 0;
        if(redisUtils.hasKey(redisKey)){
            count = (int) redisUtils.get(redisKey);
        }else{
            LogRequest logRequest = new LogRequest();
            logRequest.setQueryTime(time);
            count = logMapper.getCount(logRequest);
            redisUtils.setDay(redisKey,count,16);
        }
        return count;
    }

}
