package cn.xianyum.extension.service.impl;

import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.entity.response.RobotResponse;
import cn.xianyum.extension.service.GoldPriceService;
import cn.xianyum.extension.service.RobotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;

/**
 * @author zhangwei
 * @date 2025/7/8 23:09
 */
@Service
public class RobotServiceImpl implements RobotService {

    @Resource
    private GoldPriceService goldPriceService;


    @Override
    public RobotResponse autoReply(String content) {
        RobotResponse robotResponse = new RobotResponse();
        robotResponse.setReplyContent(content);
        if(content.contains("金价")){
            GoldPriceResponse latestPrice = goldPriceService.getLatestPrice();
            String time = DateUtils.format(latestPrice.getTime(),DateUtils.DATE_TIME_PATTERN);
            robotResponse.setReplyContent(String.format("\n当前金价：%s \n统计时间：%s",latestPrice.getLatestPrice().setScale(2, RoundingMode.HALF_UP),time));
        }
        return robotResponse;
    }
}
