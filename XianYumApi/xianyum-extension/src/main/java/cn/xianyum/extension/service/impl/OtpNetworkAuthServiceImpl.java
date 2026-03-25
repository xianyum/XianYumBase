package cn.xianyum.extension.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.extension.dao.OtpNetworkAuthMapper;
import cn.xianyum.extension.entity.po.OtpNetworkAuthEntity;
import cn.xianyum.extension.entity.request.OtpNetworkAuthRequest;
import cn.xianyum.extension.entity.response.OtpNetworkAuthResponse;
import cn.xianyum.extension.service.OtpNetworkAuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        return otpNetworkAuthMapper.insert(BeanUtil.copyProperties(request, OtpNetworkAuthEntity.class)) > 0;
    }

    @Override
    public List<OtpNetworkAuthResponse> getList() {
        LambdaQueryWrapper<OtpNetworkAuthEntity> queryWrapper = Wrappers.<OtpNetworkAuthEntity>lambdaQuery()
                .eq(OtpNetworkAuthEntity::getCreateBy, SecurityUtils.getLoginUser().getId())
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
        return otpNetworkAuthMapper.deleteById(id) > 0;
    }
}
