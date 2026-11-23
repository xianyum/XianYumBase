package cn.xianyum.system.infra.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.system.dao.ApiKeyMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

/**
 * API Key 请求量刷新任务
 * 每日 01:00 将前一天 Redis 中的调用次数累加到 api_key.total_request_count
 */
@Slf4j
@JobHandler("apiKeyStatFlushTask")
public class ApiKeyStatFlushTask implements IJobHandler {

    /** 日统计保留 7 天 */
    private static final long STAT_EXPIRE_SECONDS = 7 * 24 * 3600L;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ApiKeyMapper apiKeyMapper;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        String statDate = DateUtil.format(DateUtil.yesterday(), DatePattern.PURE_DATE_FORMAT);
        String statKey = RedisKeyEnum.API_KEY_DAY_STAT.formatKey(statDate);
        Map<Object, Object> statMap = redisUtils.hMGet(statKey);
        if (CollUtil.isEmpty(statMap)) {
            tool.info("API Key 请求量刷新：无数据，日期=" + statDate);
            return ReturnT.SUCCESS;
        }

        String doneKey = RedisKeyEnum.API_KEY_STAT_FLUSH_DONE.formatKey(statDate);
        int processedCount = 0;
        for (Map.Entry<Object, Object> entry : statMap.entrySet()) {
            String apiKey = String.valueOf(entry.getKey());
            if (redisUtils.sHasKey(doneKey, apiKey)) {
                continue;
            }
            long count = Convert.toLong(entry.getValue(), 0L);
            if (count <= 0) {
                continue;
            }
            apiKeyMapper.increaseTotalCount(apiKey, count);
            redisUtils.sSet(doneKey, apiKey);
            processedCount++;
        }
        redisUtils.expire(doneKey, STAT_EXPIRE_SECONDS);
        tool.info("API Key 请求量刷新完成，日期=" + statDate + "，处理数量=" + processedCount);
        return ReturnT.SUCCESS;
    }
}
