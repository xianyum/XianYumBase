package cn.xianyum.extension.service.impl;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.BigDecimalUtils;
import cn.xianyum.common.utils.StringUtil;
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
import java.util.Objects;

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
                .like(StringUtil.isNotEmpty(request.getVehicleNo()),EvDriveRecordsEntity::getVehicleNo,request.getVehicleNo())
                .ge(Objects.nonNull(request.getParams().get("beginTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("beginTime"))
                .le(Objects.nonNull(request.getParams().get("endTime")),EvDriveRecordsEntity::getDriveDate,request.getParams().get("endTime"))
                .orderByDesc(EvDriveRecordsEntity::getDriveDate);
        Page<EvDriveRecordsEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<EvDriveRecordsEntity> pageResult = evDriveRecordsMapper.selectPage(page, queryWrapper);
        return PageResponse.of(pageResult, EvDriveRecordsResponse.class);

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
        bean.setElectricityPerKm(BigDecimalUtils.divide(bean.getElectricityConsumed(),new BigDecimal(String.valueOf(bean.getDistanceKm()))));
        return evDriveRecordsMapper.insert(bean);
    }


    @Override
    public Integer update(EvDriveRecordsRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("id不能为空");
        }
        this.checkForDuplicateData(request);
        EvDriveRecordsEntity bean = BeanUtils.copy(request, EvDriveRecordsEntity.class);
        bean.setElectricityPerKm(BigDecimalUtils.divide(bean.getElectricityConsumed(),new BigDecimal(String.valueOf(bean.getDistanceKm()))));
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
}

