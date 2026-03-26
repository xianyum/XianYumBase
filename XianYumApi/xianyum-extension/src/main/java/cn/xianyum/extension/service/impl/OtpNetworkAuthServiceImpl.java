package cn.xianyum.extension.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.extension.dao.OtpNetworkAuthMapper;
import cn.xianyum.extension.entity.po.OtpNetworkAuthEntity;
import cn.xianyum.extension.entity.request.OtpNetworkAuthRequest;
import cn.xianyum.extension.entity.response.OtpNetworkAuthResponse;
import cn.xianyum.extension.service.OtpNetworkAuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xianyum
 * @date 2026/3/25 21:17
 */
@Service
public class OtpNetworkAuthServiceImpl implements OtpNetworkAuthService {

    @Resource
    private OtpNetworkAuthMapper otpNetworkAuthMapper;

    @Override
    public boolean save(OtpNetworkAuthRequest request) {
        request.setId(IdUtil.getSnowflakeNextIdStr());
        request.setStatus(YesOrNoEnum.YES.getStatus());
        return otpNetworkAuthMapper.insert(BeanUtil.copyProperties(request, OtpNetworkAuthEntity.class)) > 0;
    }

    @Override
    public List<OtpNetworkAuthResponse> getList() {
        LambdaQueryWrapper<OtpNetworkAuthEntity> queryWrapper = Wrappers.<OtpNetworkAuthEntity>lambdaQuery()
                .eq(OtpNetworkAuthEntity::getCreateBy, SecurityUtils.getLoginUser().getId())
                .eq(OtpNetworkAuthEntity::getStatus, YesOrNoEnum.YES.getStatus())
                .orderByDesc(OtpNetworkAuthEntity::getCreateTime);
        return BeanUtil.copyToList(otpNetworkAuthMapper.selectList(queryWrapper), OtpNetworkAuthResponse.class);
    }

    /**
     * 根据主键id删除OTP网络验证
     *
     * @param id 主键id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        LambdaUpdateWrapper<OtpNetworkAuthEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(OtpNetworkAuthEntity::getId, id);
        lambdaUpdateWrapper.set(OtpNetworkAuthEntity::getStatus, YesOrNoEnum.NO.getStatus());
        return otpNetworkAuthMapper.update(null, lambdaUpdateWrapper) > 0;
    }

    /**
     * 更新OTP网络验证
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public boolean update(OtpNetworkAuthRequest request) {
        LambdaUpdateWrapper<OtpNetworkAuthEntity> lambdaUpdateWrapper = Wrappers.<OtpNetworkAuthEntity>lambdaUpdate()
                .eq(OtpNetworkAuthEntity::getId, request.getId())
                .set(OtpNetworkAuthEntity::getSystemName, request.getSystemName());
        return otpNetworkAuthMapper.update(null, lambdaUpdateWrapper) > 0;
    }
}
