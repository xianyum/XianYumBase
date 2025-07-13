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

    @Override
    public RobotResponse autoReply(String content) {
        RobotResponse robotResponse = new RobotResponse();
        switch (content) {
            case String s when s.contains("01") -> {
                GoldPriceResponse latestPrice = goldPriceService.getLatestPrice();
                String time = DateUtils.format(latestPrice.getTime(), DateUtils.DATE_TIME_PATTERN);
                robotResponse.setReplyContent(String.format(
                        "\n当前金价：%s \n统计时间：%s",
                        latestPrice.getLatestPrice().setScale(2, RoundingMode.HALF_UP),
                        time
                ));
            }
            case String s when s.contains("02") -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(DateUtils.format(DateUtils.getMonthStartTime()),DateUtils.format(DateUtils.getMonthEndTime()));
                if(Objects.isNull(response)){
                    robotResponse.setReplyContent("\n暂未查到行驶记录");
                }else{
                    robotResponse.setReplyContent(buildEvDriveRecordsSummaryMessage(response));
                }
            }
            case String s when s.contains("03") -> {
                EvDriveRecordsSummaryResponse response = this.queryEvDriveRecordsSummary(DateUtils.format(DateUtils.getLastYearStartTime()),DateUtils.format(DateUtils.getLastYearEndTime()));
                if(Objects.isNull(response)){
                    robotResponse.setReplyContent("\n暂未查到行驶记录");
                }else{
                   robotResponse.setReplyContent(buildEvDriveRecordsSummaryMessage(response));
                }
            }
            case String s when s.contains("帮助") || s.contains("菜单") -> robotResponse.setReplyContent(buildHelpMessage());
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
                "\n总行驶里程：%s\n总耗电量：%skWh\n每公里耗电量：%skWh/km",
                records.getTotalDistanceKm(),
                records.getTotalElectricityConsumed(),
                records.getElectricityPerKm()
        );
    }



}
