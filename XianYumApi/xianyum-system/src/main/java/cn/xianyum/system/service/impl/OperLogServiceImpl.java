package cn.xianyum.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.system.dao.OperLogMapper;
import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.entity.request.OperLogRequest;
import cn.xianyum.system.entity.response.OperLogResponse;
import cn.xianyum.system.service.OperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 操作日志 Service 实现
 */
@Service
public class OperLogServiceImpl implements OperLogService {

    @Resource
    private OperLogMapper operLogMapper;

    @Resource
    private ThreadPoolTaskExecutor xianYumTaskExecutor;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MessageSender messageSender;

    @Override
    public void saveOperLog(OperLogEntity operLogEntity) {
        xianYumTaskExecutor.execute(() -> operLogMapper.insert(operLogEntity));
    }

    @Override
    public PageResponse<OperLogResponse> getPage(OperLogRequest request) {
        LambdaQueryWrapper<OperLogEntity> wrapper = Wrappers.<OperLogEntity>lambdaQuery()
                .in(CollUtil.isNotEmpty(request.getOperTypeList()), OperLogEntity::getOperType, request.getOperTypeList())
                .eq(StrUtil.isNotBlank(request.getUserName()), OperLogEntity::getUserName, request.getUserName())
                .eq(StrUtil.isNotBlank(request.getModule()), OperLogEntity::getModule, request.getModule())
                .eq(StrUtil.isNotBlank(request.getOperType()), OperLogEntity::getOperType, request.getOperType())
                .eq(request.getStatus() != null, OperLogEntity::getStatus, request.getStatus())
                .like(StrUtil.isNotBlank(request.getRequestUrl()), OperLogEntity::getRequestUrl, request.getRequestUrl())
                .orderByDesc(OperLogEntity::getId);

        appendTimeRange(wrapper, request.getStartTime(), request.getEndTime());

        Page<OperLogEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<OperLogEntity> result = operLogMapper.selectPage(page, wrapper);
        return PageResponse.of(result, OperLogResponse.class);
    }

    @Override
    public List<OperLogResponse> getVisitCountCharts() {
        Date date = new Date();
        List<String> dateStrings = DateUtils.minusDate(date, DateUtils.DATE_PATTERN, 15);
        List<OperLogResponse> responses = new ArrayList<>();
        dateStrings.forEach(p -> {
            OperLogResponse logResponse = new OperLogResponse();
            logResponse.setTime(p);
            logResponse.setVisitCount(this.getLogCountWithCache(p));
            responses.add(logResponse);
        });
        Collections.reverse(responses);
        return responses;
    }

    @Override
    public void deleteByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        LambdaQueryWrapper<OperLogEntity> wrapper = Wrappers.<OperLogEntity>lambdaQuery()
                .in(OperLogEntity::getId, Arrays.asList(ids));
        operLogMapper.delete(wrapper);
    }

    @Override
    public Long getOperLogCount() {
        LambdaQueryWrapper<OperLogEntity> queryWrapper = Wrappers.<OperLogEntity>lambdaQuery()
                .orderByDesc(OperLogEntity::getId).last("limit 1");
        Optional<OperLogEntity> first = operLogMapper.selectList(queryWrapper).stream().findFirst();
        return first.isPresent()?first.get().getId():0L;
    }

    @Override
    public Long getLogCountWithCache(String time) {
        String redisKey = RedisKeyEnum.LOG_COUNT.getKey() + time;
        Long count;
        if(redisUtils.hasKey(redisKey)){
            String o = String.valueOf(redisUtils.get(redisKey));
            count = Long.valueOf(o);
        }else{
            OperLogRequest logRequest = new OperLogRequest();
            logRequest.setQueryTime(time);
            count = operLogMapper.getCount(logRequest);
            redisUtils.setDay(redisKey,count,16);
        }
        return count;
    }

    @Override
    public void truncateLog() {
        operLogMapper.truncateLog();
    }

    @Override
    public void push() {
        DateTime now = DateTime.now();
        String nowDay = now.toString("yyyy-MM-dd");
        String oneDay = now.minusDays(1).toString("yyyy-MM-dd");
        String twoDay = now.minusDays(2).toString("yyyy-MM-dd");

        OperLogRequest logRequest = new OperLogRequest();
        logRequest.setQueryTime(nowDay);
        Long nowCount = operLogMapper.getCount(logRequest);

        Long oneCount = this.getLogCountWithCache(oneDay);

        Long twoCount = this.getLogCountWithCache(twoDay);

        Long hoopCount = oneCount - twoCount;
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

    private void appendTimeRange(LambdaQueryWrapper<OperLogEntity> wrapper, String startTime, String endTime) {
        if (StrUtil.isNotBlank(startTime)) {
            wrapper.ge(OperLogEntity::getCreateTime, DateUtil.parse(startTime));
        }
        if (StrUtil.isNotBlank(endTime)) {
            wrapper.le(OperLogEntity::getCreateTime, DateUtil.parse(endTime));
        }
    }
}
