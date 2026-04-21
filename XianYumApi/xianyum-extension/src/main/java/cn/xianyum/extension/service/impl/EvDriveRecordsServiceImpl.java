package cn.xianyum.extension.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.extension.entity.response.EvDriveRecordsAppReportResponse;
import cn.xianyum.extension.entity.response.EvDriveRecordsSummaryResponse;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.EvDriveRecordsEntity;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;
import cn.xianyum.extension.dao.EvDriveRecordsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 新能源车行驶记录(EvDriveRecords)service层实现
 *
 * @author zhangwei
 * @since 2025-03-06 20:43:41
 */
@Service
@Slf4j
public class EvDriveRecordsServiceImpl implements EvDriveRecordsService {

    @Autowired
    private EvDriveRecordsMapper evDriveRecordsMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ChatClient chatClient;

    @Value("${spring.ai.openai.chat.options.model}")
    private String aiModel;

    @Override
    public PageResponse<EvDriveRecordsResponse> getPage(EvDriveRecordsRequest request) {
        LambdaQueryWrapper<EvDriveRecordsEntity> queryWrapper = Wrappers.<EvDriveRecordsEntity>lambdaQuery()
                .like(Objects.nonNull(request.getStatus()),EvDriveRecordsEntity::getStatus,request.getStatus())
                .like(StringUtil.isNotEmpty(request.getVehicleNo()),EvDriveRecordsEntity::getVehicleNo,request.getVehicleNo())
                .ge(Objects.nonNull(request.getParams().get("beginTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("beginTime"))
                .le(Objects.nonNull(request.getParams().get("endTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("endTime"))
                .orderByDesc(EvDriveRecordsEntity::getDriveDate);
        if(CollectionUtils.isNotEmpty(request.getMatter())){
            if (request.getMatter().size() == 1) {
                queryWrapper.apply("FIND_IN_SET({0}, matter)", request.getMatter().get(0));
            } else {
                queryWrapper.nested(wrapper -> {
                    for (int i = 0; i < request.getMatter().size(); i++) {
                        String role = request.getMatter().get(i);
                        if (i == 0) {
                            wrapper.apply("FIND_IN_SET({0}, matter)", role);
                        } else {
                            wrapper.or().apply("FIND_IN_SET({0}, matter)", role);
                        }
                    }
                });
            }
        }
        Page<EvDriveRecordsEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<EvDriveRecordsEntity> pageResult = evDriveRecordsMapper.selectPage(page, queryWrapper);
        PageResponse<EvDriveRecordsResponse> response = PageResponse.of(pageResult, EvDriveRecordsResponse.class);
        // 获取汇总数据
        if(pageResult.getTotal() > 0){
            response.setOtherInfo(evDriveRecordsMapper.selectSummaryData(request));
        }
        return response;

    }


    @Override
    public EvDriveRecordsResponse getById(Long id) {
        EvDriveRecordsEntity result = evDriveRecordsMapper.selectById(id);
        EvDriveRecordsResponse response = BeanUtils.copy(result, EvDriveRecordsResponse.class);
        return response;
    }


    @Override
    public Integer save(EvDriveRecordsRequest request) {
        this.checkForDuplicateData(request);
        EvDriveRecordsEntity bean = BeanUtils.copy(request, EvDriveRecordsEntity.class);
        BigDecimal electricityPerKm = BigDecimalUtils.divide(bean.getElectricityConsumed(), new BigDecimal(String.valueOf(bean.getDistanceKm())));
        bean.setElectricityPerKm(Objects.nonNull(electricityPerKm)?electricityPerKm:bean.getElectricityConsumed());
        boolean isNormalStatus = this.checkNormalStatus(electricityPerKm);
        bean.setStatus(isNormalStatus? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
        return evDriveRecordsMapper.insert(bean);
    }


    @Override
    public Integer update(EvDriveRecordsRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("id不能为空");
        }
        this.checkForDuplicateData(request);
        EvDriveRecordsEntity bean = BeanUtils.copy(request, EvDriveRecordsEntity.class);
        BigDecimal electricityPerKm = BigDecimalUtils.divide(bean.getElectricityConsumed(), new BigDecimal(String.valueOf(bean.getDistanceKm())));
        bean.setElectricityPerKm(electricityPerKm);
        boolean isNormalStatus = this.checkNormalStatus(electricityPerKm);
        bean.setStatus(isNormalStatus? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
        return evDriveRecordsMapper.updateById(bean);
    }


    @Override
    public Integer deleteById(Long[] ids) {
        int resultCount = 0;
        for (Long id : ids) {
            resultCount = evDriveRecordsMapper.deleteById(id) + resultCount;
        }
        return resultCount;
    }

    /**
     * 校验重复数据
     *
     * @param request
     */
    @Override
    public void checkForDuplicateData(EvDriveRecordsRequest request) {
        if(Objects.isNull(request.getDistanceKm())){
            throw new SoException("行驶公里数不能为空");
        }
        if(Objects.isNull(request.getElectricityConsumed())){
            throw new SoException("消耗的电量不能为空");
        }
        if(Objects.isNull(request.getDriveDate())){
            throw new SoException("行驶的日期不能为空");
        }
        EvDriveRecordsResponse evDriveRecordsResponse = this.selectByDay(request.getDriveDate());
        boolean isRepeatData = !(Objects.isNull(evDriveRecordsResponse) || evDriveRecordsResponse.getId().equals(request.getId()));
        if(isRepeatData){
            throw new SoException("存在重复的驾驶日期数据");
        }
    }

    /**
     * 获取报表折线图
     * @param request
     * @return
     */
    @Override
    public List<Map<String, Object>> getReportLineData(EvDriveRecordsRequest request) {
        List<Map<String, Object>> resultMap =  this.evDriveRecordsMapper.selectReportLineData(request);
        return resultMap;
    }

    /**
     * 判断是否异常数据
     *
     * @param electricityPerKm
     * @return
     */
    @Override
    public boolean checkNormalStatus(BigDecimal electricityPerKm) {
        JSONObject electricityPerKmThresholdObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.ELECTRICITY_PER_KM_THRESHOLD);
        BigDecimal max = electricityPerKmThresholdObject.getBigDecimal("max");
        BigDecimal min = electricityPerKmThresholdObject.getBigDecimal("min");
        if(BigDecimalUtils.gt(electricityPerKm,max) || BigDecimalUtils.lt(electricityPerKm,min)){
            return false;
        }
        return true;
    }

    @Override
    public EvDriveRecordsResponse selectByDay(Date driveDate) {
        LambdaQueryWrapper<EvDriveRecordsEntity> queryWrapper = Wrappers.<EvDriveRecordsEntity>lambdaQuery()
                .apply("DATE(drive_date) = {0}", DateUtils.format(driveDate, DateUtils.DATE_PATTERN));
        EvDriveRecordsEntity evDriveRecordsEntity = this.evDriveRecordsMapper.selectOne(queryWrapper);
        return BeanUtils.copy(evDriveRecordsEntity, EvDriveRecordsResponse.class);
    }

    @Override
    public EvDriveRecordsAppReportResponse getAppSummaryData() {
        // 获取本月的数据
        EvDriveRecordsSummaryResponse currentMonthResponse = this.queryEvDriveRecordsSummary(DateUtils.format(DateUtils.getMonthStartTime()), DateUtils.format(DateUtils.getMonthEndTime()));
        // 获取上月的数据
        EvDriveRecordsSummaryResponse lastMonthResponse = this.queryEvDriveRecordsSummary(DateUtils.format(DateUtils.getLastMonthStartTime()), DateUtils.format(DateUtils.getLastMonthEndTime()));
        // 获取近一年的数据
        EvDriveRecordsSummaryResponse lastYearResponse = this.queryEvDriveRecordsSummary(DateUtils.format(DateUtils.getLastYearStartTime()), DateUtils.format(DateUtils.getLastYearEndTime()));
        EvDriveRecordsAppReportResponse response  = new EvDriveRecordsAppReportResponse();
        response.setLastYearResponse(lastYearResponse);
        response.setCurrentMonthResponse(currentMonthResponse);
        response.setLastMonthResponse(lastMonthResponse);
        return response;
    }

    @Override
    public EvDriveRecordsSummaryResponse queryEvDriveRecordsSummary(String startTime, String endTime) {
        EvDriveRecordsRequest request = new EvDriveRecordsRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("beginTime",startTime);
        params.put("endTime",endTime);
        request.setParams(params);
        EvDriveRecordsSummaryResponse evDriveRecordsSummaryResponse = evDriveRecordsMapper.selectSummaryData(request);
        return Objects.nonNull(evDriveRecordsSummaryResponse) ? evDriveRecordsSummaryResponse : null;
    }

    /**
     * 行驶记录AI分析
     *
     * @return
     */
    @Override
    public String aiAnalysis() {
        // 缓存键
        String cacheKey = RedisKeyEnum.EV_DRIVE_AI_ANALYSIS.getKey();
        String processingKey = RedisKeyEnum.EV_DRIVE_AI_PROCESSING.getKey();
        // 尝试从Redis读取缓存
        String cachedAnalysis = redisUtils.getString(cacheKey);
        if (StringUtil.isNotBlank(cachedAnalysis)) {
            return cachedAnalysis;
        }
        // 检查是否正在处理中
        Boolean isProcessing = redisUtils.hasKey(processingKey);
        if (Boolean.TRUE.equals(isProcessing)) {
            throw new SoException("AI正在分析中，请稍后查看结果");
        }
        // 设置处理中标志
        redisUtils.setMin(processingKey, true, 10); // 10分钟过期
        // 异步生成AI分析
        CompletableFuture.runAsync(() -> {
            try {
                LambdaQueryWrapper<EvDriveRecordsEntity> queryWrapper = Wrappers.<EvDriveRecordsEntity>lambdaQuery()
                        .orderByDesc(EvDriveRecordsEntity::getDriveDate).last("limit 31");
                // 获取近30天的数据
                List<EvDriveRecordsEntity> records = this.evDriveRecordsMapper.selectList(queryWrapper);
                StringBuilder prompt = new StringBuilder();
                prompt.append("# 你是一位专业的新能源汽车数据分析师，擅长车辆能耗分析、驾驶行为评估、趋势预测与用车建议。请根据以下数据，生成一份新能源车行驶记录分析报告\n\n");
                prompt.append("## 分析背景\n");
                prompt.append("分析车型：海豹EV550款2022款\n");
                prompt.append("地点：陕西省西安市\n");
                prompt.append("分析时间范围：最近30天\n\n");
                prompt.append("## 行驶数据\n");

                // 格式化数据
                for (EvDriveRecordsEntity record : records) {
                    prompt.append("- 日期：").append(DateUtils.format(record.getDriveDate())).append("\n");
                    prompt.append("  - 行驶里程：").append(record.getDistanceKm()).append("公里\n");
                    prompt.append("  - 消耗电量：").append(record.getElectricityConsumed()).append("度\n");
                    prompt.append("  - 电耗：").append(record.getElectricityPerKm()).append("度/公里\n");
                }
                prompt.append("## 车辆能耗与行驶行为智能分析要求\n");
                prompt.append("1. 基础信息展示：报告开头必须按顺序包含 AI模型、分析时间范围、车辆型号、使用地点、报告生成时间，格式清晰醒目。其中报告生成时间取："+DateUtils.format(new Date())+",AI模型取："+aiModel);
                prompt.append("2. 行驶行为分析：对行驶里程进行趋势分析，区分工作日与节假日的出行特征。\n");
                prompt.append("3. 电耗数据分析：对电耗数据进行趋势分析，自动识别电耗偏高异常数据并标注可能原因。\n");
                prompt.append("4. 能效指标计算：精准计算平均电耗，对比正常能效区间，评估车辆当前能耗水平。\n");
                prompt.append("5. 个性化驾驶建议：基于历史行驶与电耗数据，针对性给出节能驾驶、用车习惯优化建议\n");
                prompt.append("6. 趋势智能预测：结合上班通勤规律、节假日出行习惯，预测未来7-15天电耗与行驶里程趋势\n");
                prompt.append("7. 输出格式规范：全程使用标准Markdown格式，必须使用图表说明，合理使用emoji提升可读性，排版整洁、层级分明、无冗余内容\n");

                String content = chatClient.prompt().user(prompt.toString()).call().content();
                // 计算当前时间到今天结束的毫秒差,再转成秒
                long expireSeconds = (DateUtil.endOfDay(new Date()).getTime() - System.currentTimeMillis()) / 1000;
                redisUtils.set(cacheKey, content, Math.max(expireSeconds, 1));
            } finally {
                // 清除处理中标志
                redisUtils.del(processingKey);
            }
        });
        throw new SoException("AI正在分析中，请稍后查看结果");
    }
}
