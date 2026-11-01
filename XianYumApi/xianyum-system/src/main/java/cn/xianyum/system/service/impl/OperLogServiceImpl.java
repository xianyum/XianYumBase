package cn.xianyum.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.system.dao.OperLogMapper;
import cn.xianyum.system.entity.po.OperLogEntity;
import cn.xianyum.system.entity.request.OperLogRequest;
import cn.xianyum.system.entity.response.OperLogResponse;
import cn.xianyum.system.service.OperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
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

    @Override
    public void saveOperLog(OperLogEntity operLogEntity) {
        xianYumTaskExecutor.execute(() -> operLogMapper.insert(operLogEntity));
    }

    @Override
    public PageResponse<OperLogResponse> getPage(OperLogRequest request) {
        LambdaQueryWrapper<OperLogEntity> wrapper = Wrappers.<OperLogEntity>lambdaQuery()
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
        List<String> dateStrings = DateUtils.minusDate(new Date(), DateUtils.DATE_PATTERN, 15);
        List<OperLogResponse> responses = new ArrayList<>();
        for (String dateStr : dateStrings) {
            String startTime = dateStr + " 00:00:00";
            String endTime = dateStr + " 23:59:59";
            LambdaQueryWrapper<OperLogEntity> wrapper = Wrappers.<OperLogEntity>lambdaQuery()
                    .ge(OperLogEntity::getCreateTime, DateUtil.parse(startTime))
                    .le(OperLogEntity::getCreateTime, DateUtil.parse(endTime));
            Long count = operLogMapper.selectCount(wrapper);
            OperLogResponse response = new OperLogResponse();
            response.setVisitCount(count != null ? count : 0L);
            response.setCreateTime(DateUtil.parse(dateStr));
            responses.add(response);
        }
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
    public void truncateAll() {
        operLogMapper.delete(Wrappers.lambdaQuery());
    }

    @Override
    public Long getOperLogCount() {
        Long count = operLogMapper.selectCount(Wrappers.lambdaQuery());
        return count != null ? count : 0L;
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
