package cn.xianyum.extension.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.extension.dao.EvAutoReportMapper;
import cn.xianyum.extension.dao.EvTripMapper;
import cn.xianyum.extension.entity.po.EvAutoReportEntity;
import cn.xianyum.extension.entity.po.EvTripEntity;
import cn.xianyum.extension.infra.amap.AmapService;
import cn.xianyum.extension.service.EvTripService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 行程汇总Service实现
 *
 * @author xianyum
 * @date 2026/8/27 21:16
 */
@Service
@Slf4j
public class EvTripServiceImpl implements EvTripService {

    @Resource
    private EvTripMapper evTripMapper;

    @Resource
    private EvAutoReportMapper evAutoReportMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private AmapService amapService;

    /**
     * 行程拆分间隔：1分钟
     */
    private static final long TRIP_GAP_SECONDS = 60L;

    /**
     * 汇总行程
     *
     * @return
     */
    @Override
    public ReturnT doSummaryEvTrip() {
        try {
            // 1. 获取上次处理的时间
            LocalDateTime lastProcessedTime = LocalDateTime.of(1970, 1, 1, 0, 0);
            String lastProcessedTimeStr = redisUtils.getString(RedisKeyEnum.EV_TRIP_LAST_PROCESSED_UTC.getKey());
            if (StringUtil.isNotBlank(lastProcessedTimeStr)) {
                lastProcessedTime = LocalDateTime.parse(lastProcessedTimeStr);
            }

            // 2. 查询 is_parked=0 且 reportTime > lastProcessedTime 的行驶数据
            LambdaQueryWrapper<EvAutoReportEntity> queryWrapper = Wrappers.<EvAutoReportEntity>lambdaQuery()
                    .eq(EvAutoReportEntity::getIsCharging, 0)
                    .gt(EvAutoReportEntity::getReportTime, lastProcessedTime)
                    .orderByAsc(EvAutoReportEntity::getReportTime);
            List<EvAutoReportEntity> reportList = evAutoReportMapper.selectList(queryWrapper);

            if (CollUtil.isEmpty(reportList)) {
                log.info("No new driving data to process for trip summary");
                return ReturnT.SUCCESS;
            }

            // 3. 将数据按行程拆分
            List<List<EvAutoReportEntity>> trips = splitTrips(reportList);

            // 4. 判断最后一次行程是否还在执行中
            boolean lastTripOngoing = isLastTripOngoing();

            int savedTripCount = 0;
            LocalDateTime maxTime = lastProcessedTime;

            // 5. 处理每个行程（最后一个行程如果还在执行中则不记录）
            for (int i = 0; i < trips.size(); i++) {
                List<EvAutoReportEntity> tripData = trips.get(i).stream().filter(item->item.getIsParked() == 0).collect(Collectors.toList());
                if(CollUtil.isEmpty(tripData)){
                    continue;
                }
                LocalDateTime tripMaxTime = tripData.get(tripData.size() - 1).getReportTime();

                // 更新maxTime（即使跳过也要推进，防止重复处理）
                if (tripMaxTime.isAfter(maxTime)) {
                    maxTime = tripMaxTime;
                }

                // 如果是最后一个行程且还在执行中，跳过不记录
                if (i == trips.size() - 1 && lastTripOngoing) {
                    continue;
                }

                EvTripEntity tripEntity = buildTripEntity(tripData);
//                evTripMapper.insert(tripEntity);
                savedTripCount++;
                log.info("Saved trip: startTime={}, endTime={}, 行驶里程={}km,行驶soc:{}%-{}%, startAddress={}, endAddress={}",
                        DateUtil.formatLocalDateTime(tripEntity.getTripStartTime()), DateUtil.formatLocalDateTime(tripEntity.getTripEndTime()),
                        tripEntity.getEndOdometer().subtract(tripEntity.getStartOdometer()),tripEntity.getStartSoc(),tripEntity.getEndSoc(),
                        tripEntity.getStartAddress(),tripEntity.getEndAddress());
            }

            // 6. 更新上次处理的时间到Redis
//            redisUtils.set(RedisKeyEnum.EV_TRIP_LAST_PROCESSED_UTC.getKey(), maxTime.toString());

            log.info("Trip summary completed. Saved {} trips. Last processed time: {}", savedTripCount, maxTime);
            return ReturnT.SUCCESS;

        } catch (Exception e) {
            log.error("Error during trip summary job", e);
            return ReturnT.FAILURE;
        }
    }

    /**
     * 将遥测数据按行程拆分
     * 规则：
     * 1. 时间间隔超过1分钟视为新行程
     * 2. 跨天（日期变更，即过了凌晨0点）视为新行程
     */
    private List<List<EvAutoReportEntity>> splitTrips(List<EvAutoReportEntity> reportList) {
        List<List<EvAutoReportEntity>> trips = new ArrayList<>();
        List<EvAutoReportEntity> currentTrips = new ArrayList<>();

        for (int i = 0; i < reportList.size(); i++) {
            EvAutoReportEntity current = reportList.get(i);

            if (CollUtil.isEmpty(currentTrips)) {
                currentTrips.add(current);
                continue;
            }

            EvAutoReportEntity previous = reportList.get(i - 1);

            // 判断是否需要开启新行程：
            // 1. 时间间隔超过1分钟
            long durationSeconds = Duration.between(previous.getReportTime(), current.getReportTime()).getSeconds();
            boolean newTripByGap = durationSeconds > TRIP_GAP_SECONDS;

            // 2. 跨天判断（凌晨跨日）
            boolean newTripByDay = isCrossDay(previous.getReportTime(), current.getReportTime());

            if (newTripByGap || newTripByDay) {
                trips.add(new ArrayList<>(currentTrips));
                currentTrips.clear();
            }

            currentTrips.add(current);
        }

        // 添加最后一个行程
        if (!currentTrips.isEmpty()) {
            trips.add(currentTrips);
        }

        return trips;
    }

    /**
     * 判断两个LocalDateTime是否跨天（跨了凌晨0点）
     */
    private boolean isCrossDay(LocalDateTime prevTime, LocalDateTime currTime) {
        LocalDate prevDate = prevTime.toLocalDate();
        LocalDate currDate = currTime.toLocalDate();
        return !prevDate.isEqual(currDate);
    }

    /**
     * 判断最后一次行程是否还在执行中
     * 通过Redis中缓存的最新上报数据的时间与当前时间比较，
     * 如果间隔在1分钟以内，则认为车辆还在行驶中
     */
    private boolean isLastTripOngoing() {
        try {
            String latestReportJson = redisUtils.getString(RedisKeyEnum.EV_TRIP_LATEST_REPORT.getKey());
            if (StringUtil.isBlank(latestReportJson)) {
                return false;
            }

            EvAutoReportEntity evAutoReportEntity = JSON.parseObject(latestReportJson, EvAutoReportEntity.class);
            LocalDateTime latestReportTime = evAutoReportEntity.getReportTime();
            if (Objects.isNull(latestReportTime)) {
                return false;
            }
            long durationSeconds = Duration.between(latestReportTime, LocalDateTime.now()).getSeconds();
            return durationSeconds <= TRIP_GAP_SECONDS;
        } catch (Exception e) {
            log.warn("Failed to check if last trip is ongoing", e);
            return false;
        }
    }

    /**
     * 根据行程数据构建行程实体
     */
    private EvTripEntity buildTripEntity(List<EvAutoReportEntity> tripData) {
        EvAutoReportEntity start = tripData.get(0);
        EvAutoReportEntity end = tripData.get(tripData.size() - 1);

        EvTripEntity entity = new EvTripEntity();
        entity.setTripStartTime(start.getReportTime());
        entity.setTripEndTime(end.getReportTime());

        // SOC
        entity.setStartSoc(start.getSoc());
        entity.setEndSoc(end.getSoc());

        // 正向：第一个至少一个坐标不为null
        Optional<EvAutoReportEntity> firstValidGeo = tripData.stream().filter(r -> r.getLat() != null || r.getLon() != null).findFirst();

        // 反向：从尾部向前找第一个有效坐标，找到立即终止
        Optional<EvAutoReportEntity> lastValidGeo = IntStream.rangeClosed(0, tripData.size() - 1).mapToObj(i -> tripData.get(tripData.size() - 1 - i)).filter(r -> r.getLat() != null || r.getLon() != null).findFirst();

        // 经纬度坐标
        EvAutoReportEntity startGeo = firstValidGeo.orElse(start);
        EvAutoReportEntity endGeo = lastValidGeo.orElse(end);
        entity.setStartLat(startGeo.getLat());
        entity.setStartLon(startGeo.getLon());
        entity.setEndLat(endGeo.getLat());
        entity.setEndLon(endGeo.getLon());
        entity.setStartAddress(amapService.getFormattedAddress(startGeo.getLon().toString(), startGeo.getLat().toString()));
        entity.setEndAddress(amapService.getFormattedAddress(endGeo.getLon().toString(), endGeo.getLat().toString()));

        // 里程
        entity.setStartOdometer(start.getOdometer());
        entity.setEndOdometer(end.getOdometer());

        // 理论消耗电量 = (起始SOC - 结束SOC) * 电池容量 / 100
        if (start.getSoc() != null && end.getSoc() != null && start.getCapacity() != null) {
            int socDiff = start.getSoc() - end.getSoc();
            if (socDiff > 0) {
                BigDecimal consumeKwh = start.getCapacity()
                        .multiply(new BigDecimal(socDiff))
                        .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                entity.setTripConsumeKwh(consumeKwh);
            }
        }

        // 电池温度统计
        List<BigDecimal> battTemps = tripData.stream().map(EvAutoReportEntity::getBattTemp).filter(Objects::nonNull).toList();
        if (CollUtil.isNotEmpty(battTemps)) {
            BigDecimal min = battTemps.stream().min(BigDecimal::compareTo).get();
            BigDecimal max = battTemps.stream().max(BigDecimal::compareTo).get();
            BigDecimal sum = battTemps.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(battTemps.size()), 2, RoundingMode.HALF_UP);
            entity.setMinBattTemp(min);
            entity.setMaxBattTemp(max);
            entity.setAvgBattTemp(avg);
        }

        return entity;
    }
}
