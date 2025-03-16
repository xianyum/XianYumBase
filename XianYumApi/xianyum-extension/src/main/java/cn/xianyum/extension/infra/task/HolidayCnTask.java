package cn.xianyum.extension.infra.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.*;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.Map;


/**
 * 中国法定节假日获取
 * @author zhangwei
 * @date 2025/3/14 20:38
 */
@JobHandler("holidayCnTask")
@Slf4j
public class HolidayCnTask implements IJobHandler {

    @Override
    public ReturnT execute(Map<String, String> jobParamsMap, SchedulerTool tool) throws Exception {
        String year = DateUtils.format(new Date(), DateUtils.YYYY);
        String formatHolidayUrl = String.format(Constants.HOLIDAY_URL, year);
        String result = HttpUtils.getHttpInstance().sync(formatHolidayUrl).get().getBody().toString();
        if(StringUtil.isEmpty(result)){
            return ReturnT.FAILURE;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String holidayJson = jsonObject.getString("holiday");
        if(StringUtil.isNotBlank(holidayJson)){
            SystemConstantUtils.saveSystemConstant(SystemConstantKeyEnum.HOLIDAY_CN,holidayJson,YesOrNoEnum.YES.getStatus());
        }
        return ReturnT.SUCCESS;
    }
}
