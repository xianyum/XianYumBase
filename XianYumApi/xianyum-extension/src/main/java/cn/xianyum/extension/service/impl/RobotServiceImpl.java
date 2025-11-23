package cn.xianyum.extension.service.impl;

import cn.xianyum.common.utils.BigDecimalUtils;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.extension.dao.EvDriveRecordsMapper;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsSummaryResponse;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.entity.response.RobotResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;
import cn.xianyum.extension.service.GoldPriceService;
import cn.xianyum.extension.service.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 匹配 #04 + 日期 + 两个数字（完整格式，前面可允许前导空白）
    // 分组1：日期（yyyy-MM-dd），分组2：第一个数字，分组3：第二个数字
    private static final Pattern PATTERN_04_FULL = Pattern.compile("^\\s*#04[\\p{Z}\\s]+(\\d{4}-\\d{2}-\\d{2})[\\p{Z}\\s]+(\\d+)[\\p{Z}\\s]+(\\d+)$");

    // 匹配+ 帮助/菜单（作为首个指令，前面可无内容，也可允许前导空白）
    private static final Pattern PATTERN_HELP = Pattern.compile("^\\s*(帮助|菜单)(\\s|$).*");

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
                        : buildEvDriveRecordsSummaryMessage(response)
                );
            }
            // 匹配包含03但不符合完整格式的内容
            case String s when PATTERN_03_SIMPLE.matcher(s).matches() -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(
                        DateUtils.format(DateUtils.getLastYearStartTime()),
                        DateUtils.format(DateUtils.getLastYearEndTime())
                );
                robotResponse.setReplyContent(Objects.isNull(response)
                        ? "\n暂未查到行驶记录"
                        : buildEvDriveRecordsSummaryMessage(response)
                );
            }
            // 匹配03+日期+数字的完整格式（优先匹配）
            case String s when PATTERN_04_FULL.matcher(s).matches() -> {
                var matcher = PATTERN_04_FULL.matcher(s);
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
            default -> robotResponse.setReplyContent("\n请发送帮助查看功能");
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
            return saveCount > 0 ? "保存成功" : "保存失败";
        }catch (Exception e){
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
                "\n#03：查看近一年行驶记录" +
                "\n#04：保存记录(例2025-11-11 100 20)";
    }

    // 构建行驶记录消息
    private String buildEvDriveRecordsSummaryMessage(EvDriveRecordsSummaryResponse records) {
        return String.format(
                "\n总行驶里程：%s km\n总耗电量：%skWh\n每公里耗电量：%skWh/km",
                records.getTotalDistanceKm(),
                records.getTotalElectricityConsumed(),
                records.getElectricityPerKm()
        );
    }



}
