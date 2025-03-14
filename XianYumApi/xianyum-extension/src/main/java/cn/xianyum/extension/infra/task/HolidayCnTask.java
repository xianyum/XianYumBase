package cn.xianyum.extension.infra.task;

import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.SystemConstantUtils;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
        int year = DateTime.now().getYear();
        String formatHolidayUrl = String.format(Constants.HOLIDAY_URL, year);
        String result = HttpUtils.getHttpInstance().sync(formatHolidayUrl).get().getBody().toString();
        if(StringUtil.isEmpty(result)){
            return ReturnT.FAILURE;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String days = jsonObject.getString("days");
        SystemConstantUtils.saveSystemConstant(SystemConstantKeyEnum.HOLIDAY_CN,days, YesOrNoEnum.YES.getStatus());
        return ReturnT.SUCCESS;
    }
}
