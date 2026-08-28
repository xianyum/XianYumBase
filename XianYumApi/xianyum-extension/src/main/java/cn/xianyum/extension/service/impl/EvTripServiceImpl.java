package cn.xianyum.extension.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.extension.dao.EvAutoReportMapper;
import cn.xianyum.extension.dao.EvTripMapper;
import cn.xianyum.extension.entity.po.EvAutoReportEntity;
import cn.xianyum.extension.entity.po.EvTripEntity;
import cn.xianyum.extension.entity.request.EvTripRequest;
import cn.xianyum.extension.entity.response.EvTripResponse;
import cn.xianyum.extension.entity.response.EvTripTrackResponse;
import cn.xianyum.extension.infra.amap.AmapService;
import cn.xianyum.extension.service.EvTripService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        LocalDateTime maxTime = LocalDateTime.of(1970, 1, 1, 0, 0);
        try {
            // 1. 获取上次处理的时间
            String lastProcessedTimeStr = redisUtils.getString(RedisKeyEnum.EV_TRIP_LAST_PROCESSED_UTC.getKey());
            if (StringUtil.isNotBlank(lastProcessedTimeStr)) {
                maxTime = LocalDateTime.parse(lastProcessedTimeStr);
            }

            // 2. 查询 is_parked=0 且 reportTime > lastProcessedTime 的行驶数据
            LambdaQueryWrapper<EvAutoReportEntity> queryWrapper = Wrappers.<EvAutoReportEntity>lambdaQuery()
                    .eq(EvAutoReportEntity::getIsCharging, 0)
                    .gt(EvAutoReportEntity::getReportTime, maxTime)
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
                evTripMapper.insert(tripEntity);
                savedTripCount++;
                log.info("保存行程：开始时间={}，结束时间={}，行驶里程={}公里，行驶SOC：{}%‑{}%，起点地址={}，终点地址={}",
                        DateUtil.formatLocalDateTime(tripEntity.getTripStartTime()),
                        DateUtil.formatLocalDateTime(tripEntity.getTripEndTime()),
                        tripEntity.getEndOdometer().subtract(tripEntity.getStartOdometer()),
                        tripEntity.getStartSoc(),
                        tripEntity.getEndSoc(),
                        tripEntity.getStartAddress(),
                        tripEntity.getEndAddress());
            }


            log.info("Trip summary completed. Saved {} trips. Last processed time: {}", savedTripCount, maxTime);
            return ReturnT.SUCCESS;

        } catch (Exception e) {
            log.error("Error during trip summary job", e);
            return ReturnT.FAILURE;
        }finally {
            // 无论insert成功/失败、整体异常，都更新redis时间，避免重复消费旧报文
            redisUtils.set(RedisKeyEnum.EV_TRIP_LAST_PROCESSED_UTC.getKey(), maxTime.toString());
        }
    }

    /**
     * 分页查询行程
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @Override
    public PageResponse<EvTripResponse> getPage(EvTripRequest request) {
        String beginTime = request.getParams().get("beginTime") != null ? request.getParams().get("beginTime").toString() : null;
        String endTime = request.getParams().get("endTime") != null ? request.getParams().get("endTime").toString() : null;
        LambdaQueryWrapper<EvTripEntity> queryWrapper = Wrappers.<EvTripEntity>lambdaQuery()
                .ge(StringUtil.isNotEmpty(beginTime), EvTripEntity::getTripStartTime, beginTime)
                .le(StringUtil.isNotEmpty(endTime), EvTripEntity::getTripEndTime, endTime)
                .orderByDesc(EvTripEntity::getTripStartTime);
        Page<EvTripEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<EvTripEntity> pageResult = evTripMapper.selectPage(page, queryWrapper);
        return PageResponse.of(pageResult, EvTripResponse.class);
    }

    /**
     * 根据ID查询行程详情
     *
     * @param id 主键
     * @return 行程详情
     */
    @Override
    public EvTripResponse getById(Long id) {
        EvTripEntity entity = evTripMapper.selectById(id);
        EvTripResponse response = BeanUtil.copyProperties(entity, EvTripResponse.class);
        if(Objects.isNull(entity)){
            return response;
        }
        // 查询该行程时间范围内、is_parked=0 的行驶数据
        LambdaQueryWrapper<EvAutoReportEntity> queryWrapper = Wrappers.<EvAutoReportEntity>lambdaQuery()
                .eq(EvAutoReportEntity::getIsParked, 0)
                .ge(EvAutoReportEntity::getReportTime, response.getTripStartTime())
                .le(EvAutoReportEntity::getReportTime, response.getTripEndTime())
                .isNotNull(EvAutoReportEntity::getLat)
                .isNotNull(EvAutoReportEntity::getLon)
                .orderByAsc(EvAutoReportEntity::getReportTime);

        List<EvAutoReportEntity> reportList = evAutoReportMapper.selectList(queryWrapper);
        List<EvTripTrackResponse> trackList = BeanUtil.copyToList(reportList, EvTripTrackResponse.class);
        // 使用 Douglas-Peucker 算法抽稀轨迹点
        List<EvTripTrackResponse> simplifiedTrack = douglasPeucker(trackList, 0.00005);
        response.setTrackList(simplifiedTrack);
        return response;
    }

    /**
     * Douglas-Peucker 轨迹抽稀算法
     * 原理：递归地找到距离首尾连线最远的点，如果距离大于阈值则保留该点，继续递归
     *
     * @param trackList 原始轨迹点列表
     * @param tolerance 容差阈值（经度/纬度单位，如 0.00005 约等于 5米）
     * @return 抽稀后的轨迹点列表
     */
    private List<EvTripTrackResponse> douglasPeucker(List<EvTripTrackResponse> trackList, double tolerance) {
        if (trackList == null || trackList.size() <= 2) {
            return trackList != null ? new ArrayList<>(trackList) : new ArrayList<>();
        }

        // 找到距离首尾连线最远的点
        int maxIndex = 0;
        double maxDistance = 0;
        EvTripTrackResponse firstPoint = trackList.get(0);
        EvTripTrackResponse lastPoint = trackList.get(trackList.size() - 1);

        for (int i = 1; i < trackList.size() - 1; i++) {
            double distance = calculatePointToLineDistance(
                    trackList.get(i), firstPoint, lastPoint);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxIndex = i;
            }
        }

        // 如果最大距离大于阈值，则递归处理
        if (maxDistance > tolerance) {
            List<EvTripTrackResponse> leftPart = douglasPeucker(
                    trackList.subList(0, maxIndex + 1), tolerance);
            List<EvTripTrackResponse> rightPart = douglasPeucker(
                    trackList.subList(maxIndex, trackList.size()), tolerance);

            // 合并结果，避免重复添加分割点
            List<EvTripTrackResponse> result = new ArrayList<>(leftPart);
            for (int i = 1; i < rightPart.size(); i++) {
                result.add(rightPart.get(i));
            }
            return result;
        } else {
            // 所有点都在阈值范围内，只保留首尾两点
            List<EvTripTrackResponse> result = new ArrayList<>();
            result.add(firstPoint);
            result.add(lastPoint);
            return result;
        }
    }

    /**
     * 计算点到线段的距离
     * 使用欧式距离近似（经纬度坐标在小范围内可近似为平面坐标）
     *
     * @param point      待计算点
     * @param lineStart  线段起点
     * @param lineEnd    线段终点
     * @return 距离值
     */
    private double calculatePointToLineDistance(EvTripTrackResponse point,
                                                 EvTripTrackResponse lineStart,
                                                 EvTripTrackResponse lineEnd) {
        double x0 = point.getLon().doubleValue();
        double y0 = point.getLat().doubleValue();
        double x1 = lineStart.getLon().doubleValue();
        double y1 = lineStart.getLat().doubleValue();
        double x2 = lineEnd.getLon().doubleValue();
        double y2 = lineEnd.getLat().doubleValue();

        // 分母为0时，所有点共线
        double denominator = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        if (denominator == 0) {
            return Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
        }

        // 点到直线的距离
        double numerator = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - x1 * y2);
        return numerator / denominator;
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
