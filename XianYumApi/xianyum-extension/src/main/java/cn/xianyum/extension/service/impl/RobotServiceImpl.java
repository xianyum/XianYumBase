package cn.xianyum.extension.service.impl;

import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.extension.dao.EvDriveRecordsMapper;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsSummaryResponse;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.entity.response.RobotResponse;
import cn.xianyum.extension.service.GoldPriceService;
import cn.xianyum.extension.service.RobotService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
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

    // 匹配 @用户名 + （任意空白/分隔符） + 01/金价（作为首个指令）
    private static final Pattern PATTERN_01 = Pattern.compile("^@[^\\s]+[\\p{Z}\\s]+(01|金价)\\b.*");

    // 匹配 @用户名 + （任意空白/分隔符） + 02（作为首个指令）
    private static final Pattern PATTERN_02 = Pattern.compile("^@[^\\s]+[\\p{Z}\\s]+02\\b.*");

    // 匹配 @用户名 + （任意空白/分隔符） + 03（作为首个指令）
    private static final Pattern PATTERN_03_SIMPLE = Pattern.compile("^@[^\\s]+[\\p{Z}\\s]+03\\b.*");

    // 匹配 @用户名 + （任意空白/分隔符） + 04 + 日期 + 两个数字（完整格式）
    private static final Pattern PATTERN_04_FULL = Pattern.compile("^@[^\\s]+[\\p{Z}\\s]+04[\\p{Z}\\s]+(\\d{4}-\\d{2}-\\d{2})[\\p{Z}\\s]+(\\d+)[\\p{Z}\\s]+(\\d+)$");

    // 匹配 @用户名 + （任意空白/分隔符） + 帮助/菜单（作为首个指令）
    private static final Pattern PATTERN_HELP = Pattern.compile("^@[^\\s]+[\\p{Z}\\s]+(帮助|菜单)\\b.*");
    
    @Override
    public RobotResponse autoReply(String content) {
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
                String date = matcher.group(1);
                int param1 = Integer.parseInt(matcher.group(2));
                int param2 = Integer.parseInt(matcher.group(3));
                // 处理带参数的04逻辑（示例）
                robotResponse.setReplyContent(String.format(
                        "\n04带参数查询：日期=%s, 参数1=%d, 参数2=%d",
                        date, param1, param2
                ));
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
                "\n01：查看金价" +
                "\n02：查看本月行驶记录" +
                "\n03：查看近一年行驶记录" +
                "\n04：保存某天行驶记录";
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
