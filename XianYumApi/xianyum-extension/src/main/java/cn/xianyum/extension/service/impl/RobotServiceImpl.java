package cn.xianyum.extension.service.impl;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.utils.BigDecimalUtils;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.dao.EvDriveRecordsMapper;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.extension.entity.response.EvDriveRecordsSummaryResponse;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.entity.response.RobotResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;
import cn.xianyum.extension.service.GoldPriceService;
import cn.xianyum.extension.service.RobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author zhangwei
 * @date 2025/7/8 23:09
 */
@Service
@Slf4j
public class RobotServiceImpl implements RobotService {

    @Resource
    private GoldPriceService goldPriceService;

    @Resource
    private EvDriveRecordsMapper evDriveRecordsMapper;

    @Resource
    private EvDriveRecordsService evDriveRecordsService;

    // 匹配 # + 01/金价（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_01 = Pattern.compile("^\\s*#(01|金价)\\b.*");

    // 匹配 # + 02（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_02 = Pattern.compile("^\\s*#02\\b.*");

    // 匹配 # + 03（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_03_SIMPLE = Pattern.compile("^\\s*#03\\b.*");

    // 匹配 # + 04（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_04_SIMPLE = Pattern.compile("^\\s*#04\\b.*");

    // 匹配 #05 + 日期 + 两个数字（完整格式，前面可允许前导空白）
    private static final Pattern PATTERN_05_FULL = Pattern.compile("^\\s*#05[\\p{Z}\\s]+(\\d{4}-\\d{2}-\\d{2})[\\p{Z}\\s]+(\\d+)[\\p{Z}\\s]+(\\d+(\\.\\d+)?)$");

    // 匹配+ 帮助/菜单（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_HELP = Pattern.compile("^\\s*#(帮助|菜单)(\\s|$).*");

    @Override
    public RobotResponse autoReply(String content) {
        content = content.replaceAll("[\\p{Z}\\s]+", " ");
        RobotResponse robotResponse = new RobotResponse();
        switch (content) {
            // 匹配包含01的内容
            case String s when PATTERN_01.matcher(s).matches() -> {
                GoldPriceResponse latestPrice = goldPriceService.getLatestPrice();
                String time = DateUtils.format(latestPrice.getTime(), DateUtils.DATE_TIME_PATTERN);
                robotResponse.setReplyContent(String.format(
                        "\n当前金价：%s \n统计时间：%s",
                        latestPrice.getLatestPrice().setScale(2, RoundingMode.HALF_UP),
                        time
                ));
            }
            // 匹配包含02的内容
            case String s when PATTERN_02.matcher(s).matches() -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(
                        DateUtils.format(DateUtils.getMonthStartTime()),
                        DateUtils.format(DateUtils.getMonthEndTime())
                );
                robotResponse.setReplyContent(Objects.isNull(response)
                        ? "\n暂未查到行驶记录"
                        : buildEvDriveRecordsSummaryMessage(response,"本月")
                );
            }
            // 匹配包含03但不符合完整格式的内容
            case String s when PATTERN_03_SIMPLE.matcher(s).matches() -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(
                        DateUtils.format(DateUtils.getLastMonthStartTime()),
                        DateUtils.format(DateUtils.getLastMonthEndTime())
                );
                robotResponse.setReplyContent(Objects.isNull(response)
                        ? "\n暂未查到行驶记录"
                        : buildEvDriveRecordsSummaryMessage(response,"上月")
                );
            }
            // 匹配包含04但不符合完整格式的内容
            case String s when PATTERN_04_SIMPLE.matcher(s).matches() -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(
                        DateUtils.format(DateUtils.getLastYearStartTime()),
                        DateUtils.format(DateUtils.getLastYearEndTime())
                );
                robotResponse.setReplyContent(Objects.isNull(response)
                        ? "\n暂未查到行驶记录"
                        : buildEvDriveRecordsSummaryMessage(response,"近一年")
                );
            }
            // 匹配05+日期+数字的完整格式（优先匹配）
            case String s when PATTERN_05_FULL.matcher(s).matches() -> {
                var matcher = PATTERN_05_FULL.matcher(s);
                matcher.matches(); // 已匹配，直接提取分组
                String driveDateStr = matcher.group(1);
                // 行驶的公里数
                Integer distanceKm = Integer.parseInt(matcher.group(2));
                // 行驶消耗电量
                BigDecimal electricityConsumed = BigDecimalUtils.formatString(matcher.group(3));
                robotResponse.setReplyContent(this.saveEvDriveRecords(driveDateStr,distanceKm,electricityConsumed));
            }

            // 匹配帮助/菜单
            case String s when PATTERN_HELP.matcher(s).matches() -> {
                robotResponse.setReplyContent(buildHelpMessage());
            }
            // 默认情况
            default -> robotResponse.setReplyContent("\n请发送[#帮助]查看功能");
        }
        return robotResponse;
    }


    /**
     * 返回replyContent
     * @param driveDateStr
     * @param distanceKm
     * @param electricityConsumed
     * @return
     */
    private String saveEvDriveRecords(String driveDateStr, Integer distanceKm, BigDecimal electricityConsumed) {
        try {
            EvDriveRecordsRequest evDriveRecordsRequest = new EvDriveRecordsRequest();
            evDriveRecordsRequest.setMatter(Collections.singletonList("10"));
            evDriveRecordsRequest.setElectricityConsumed(electricityConsumed);
            evDriveRecordsRequest.setDistanceKm(distanceKm);
            evDriveRecordsRequest.setDriveDate(DateUtils.stringToDate(driveDateStr, DateUtils.DATE_PATTERN));
            Integer saveCount = this.evDriveRecordsService.save(evDriveRecordsRequest);
            String successMessage = "\n保存成功！\nid：%s\n公里数：%skm\n电耗：%skWH\n每公里电耗：%skWH/km\n日期：%s";
            if(saveCount > 0){
                EvDriveRecordsResponse response = this.evDriveRecordsService.selectByDay(evDriveRecordsRequest.getDriveDate());
                return String.format(successMessage,response.getId(), response.getDistanceKm(),response.getElectricityConsumed(), response.getElectricityPerKm(),driveDateStr);
            }
            return Constants.SAVE_ERROR_MESSAGE;
        }catch (Exception e){
            log.error("机器人保存行驶记录异常,{},{},{}",driveDateStr,distanceKm,electricityConsumed,e);
            return e.getMessage();
        }
    }

    /**
     * 查询车辆行驶记录
     * @param startTime
     * @param endTime
     * @return
     */
    private EvDriveRecordsSummaryResponse queryEvDriveRecordsSummary(String startTime, String endTime) {
        EvDriveRecordsRequest request = new EvDriveRecordsRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("beginTime",startTime);
        params.put("endTime",endTime);
        request.setParams(params);
        return evDriveRecordsMapper.selectSummaryData(request);
    }

    // 构建帮助信息
    private String buildHelpMessage() {
        return "\n支持以下功能(请发送命令查看)：" +
                "\n#01：查看金价" +
                "\n#02：查看本月行驶记录" +
                "\n#03：查看上个月行驶记录" +
                "\n#04：查看近一年行驶记录" +
                "\n#05：保存记录(例2025-11-11 100 20)";
    }

    // 构建行驶记录消息
    private String buildEvDriveRecordsSummaryMessage(EvDriveRecordsSummaryResponse records, String timeRangeStr) {
        return String.format(
                "\n总行驶里程：%skm\n总耗电量：%skWh\n每公里耗电量：%skWh/km\n时间范围：%s",
                records.getTotalDistanceKm(),
                records.getTotalElectricityConsumed(),
                records.getElectricityPerKm(),
                timeRangeStr
        );
    }



}
