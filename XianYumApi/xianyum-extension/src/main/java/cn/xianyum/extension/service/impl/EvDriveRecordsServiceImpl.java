package cn.xianyum.extension.service.impl;

import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.BigDecimalUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.SystemConstantUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.EvDriveRecordsEntity;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;
import cn.xianyum.extension.dao.EvDriveRecordsMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新能源车行驶记录(EvDriveRecords)service层实现
 *
 * @author makejava
 * @since 2025-03-06 20:43:41
 */
@Service
@Slf4j
public class EvDriveRecordsServiceImpl implements EvDriveRecordsService {

    @Autowired
    private EvDriveRecordsMapper evDriveRecordsMapper;

    @Override
    public PageResponse<EvDriveRecordsResponse> getPage(EvDriveRecordsRequest request) {
        LambdaQueryWrapper<EvDriveRecordsEntity> queryWrapper = Wrappers.<EvDriveRecordsEntity>lambdaQuery()
                .like(Objects.nonNull(request.getStatus()),EvDriveRecordsEntity::getStatus,request.getStatus())
                .like(StringUtil.isNotEmpty(request.getVehicleNo()),EvDriveRecordsEntity::getVehicleNo,request.getVehicleNo())
                .ge(Objects.nonNull(request.getParams().get("beginTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("beginTime"))
                .le(Objects.nonNull(request.getParams().get("endTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("endTime"))
                .orderByDesc(EvDriveRecordsEntity::getDriveDate);
        Page<EvDriveRecordsEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<EvDriveRecordsEntity> pageResult = evDriveRecordsMapper.selectPage(page, queryWrapper);
        PageResponse<EvDriveRecordsResponse> response = PageResponse.of(pageResult, EvDriveRecordsResponse.class);
        // 获取汇总数据
        if(pageResult.getTotal() > 0){
            Map<String,Object> otherInfoMap = evDriveRecordsMapper.selectSummaryData(request);
            response.setOtherInfo(otherInfoMap);
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
        bean.setElectricityPerKm(electricityPerKm);
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
        LambdaQueryWrapper<EvDriveRecordsEntity> queryWrapper = Wrappers.<EvDriveRecordsEntity>lambdaQuery()
                .eq(EvDriveRecordsEntity::getDriveDate,request.getDriveDate());
        EvDriveRecordsEntity evDriveRecordsEntity = this.evDriveRecordsMapper.selectOne(queryWrapper);
        boolean isRepeatData = !(Objects.isNull(evDriveRecordsEntity) || evDriveRecordsEntity.getId().equals(request.getId()));
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
        request.setStatus(YesOrNoEnum.YES.getStatus());
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
        JSONObject electricityPerKmThresholdObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.electricity_per_km_threshold);
        BigDecimal max = electricityPerKmThresholdObject.getBigDecimal("max");
        BigDecimal min = electricityPerKmThresholdObject.getBigDecimal("min");
        if(BigDecimalUtils.gt(electricityPerKm,max) || BigDecimalUtils.lt(electricityPerKm,min)){
            return false;
        }
        return true;
    }
}

