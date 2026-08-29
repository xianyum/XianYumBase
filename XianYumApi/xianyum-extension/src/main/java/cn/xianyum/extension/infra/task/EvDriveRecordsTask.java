package cn.xianyum.extension.infra.task;

import cn.hutool.core.collection.CollUtil;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.extension.dao.EvTripMapper;
import cn.xianyum.extension.entity.po.EvTripEntity;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@JobHandler("evDriveRecordsDailyTask")
public class EvDriveRecordsTask implements IJobHandler {

    @Resource
    private EvTripMapper evTripMapper;

    @Resource
    private EvDriveRecordsService evDriveRecordsService;

    @Override
    public ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<EvTripEntity> queryWrapper = Wrappers.<EvTripEntity>lambdaQuery()
                .ge(EvTripEntity::getTripStartTime, yesterdayStart)
                .lt(EvTripEntity::getTripStartTime, todayStart);
        List<EvTripEntity> tripList = evTripMapper.selectList(queryWrapper);

        if (CollUtil.isEmpty(tripList)) {
            return ReturnT.SUCCESS;
        }
        BigDecimal totalDistance = BigDecimal.ZERO;
        BigDecimal totalElectricity = BigDecimal.ZERO;
        for (EvTripEntity trip : tripList) {
            if (trip.getStartOdometer() != null && trip.getEndOdometer() != null) {
                BigDecimal tripDistance = trip.getEndOdometer().subtract(trip.getStartOdometer());
                if (tripDistance.compareTo(BigDecimal.ZERO) > 0) {
                    totalDistance = totalDistance.add(tripDistance);
                }
            }
            if (trip.getTripConsumeKwh() != null) {
                totalElectricity = totalElectricity.add(trip.getTripConsumeKwh());
            }
        }
        int distanceKm = totalDistance.intValue();
        if (distanceKm <= 0 && totalElectricity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnT.SUCCESS;
        }

        Date driveDate = Date.from(yesterdayStart.atZone(ZoneId.systemDefault()).toInstant());
        EvDriveRecordsResponse existing = evDriveRecordsService.selectByDay(driveDate);
        if (existing != null) {
            return ReturnT.SUCCESS;
        }
        EvDriveRecordsRequest request = new EvDriveRecordsRequest();
        request.setDriveDate(driveDate);
        request.setDistanceKm(distanceKm);
        request.setElectricityConsumed(totalElectricity);
        evDriveRecordsService.save(request);
        return ReturnT.SUCCESS;
    }
}
