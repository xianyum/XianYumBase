package cn.xianyum.extension.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.AppVersionCompareUtil;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.common.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.AppVersionControlEntity;
import cn.xianyum.extension.entity.request.AppVersionControlRequest;
import cn.xianyum.extension.entity.response.AppVersionControlResponse;
import cn.xianyum.extension.service.AppVersionControlService;
import cn.xianyum.extension.dao.AppVersionControlMapper;
import org.springframework.stereotype.Service;
import java.util.Objects;

/**
 * APP版本管理(AppVersionControl)service层实现
 *
 * @author zhangwei
 * @since 2026-01-23 17:28:11
 */
@Service
@Slf4j
public class AppVersionControlServiceImpl implements AppVersionControlService {

    @Autowired
    private AppVersionControlMapper appVersionControlMapper;

    @Override
    public PageResponse<AppVersionControlResponse> getPage(AppVersionControlRequest request) {
        LambdaQueryWrapper<AppVersionControlEntity> queryWrapper = Wrappers.<AppVersionControlEntity>lambdaQuery()
                .eq(StringUtil.isNotBlank(request.getAppId()),AppVersionControlEntity::getAppId,request.getAppId())
                .orderByDesc(AppVersionControlEntity::getCreateTime);
        Page<AppVersionControlEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<AppVersionControlEntity> pageResult = appVersionControlMapper.selectPage(page, queryWrapper);
        return PageResponse.of(pageResult, AppVersionControlResponse.class);

    }


    @Override
    public AppVersionControlResponse getById(Long id) {
        AppVersionControlEntity result = appVersionControlMapper.selectById(id);
        AppVersionControlResponse response = BeanUtils.copy(result, AppVersionControlResponse.class);
        if(Objects.nonNull(response)){
            response.setFileInfo(FileUtils.selectFileById(response.getPackageFileId()));
        }
        return response;
    }


    @Override
    public Integer save(AppVersionControlRequest request) {
        AppVersionControlEntity bean = BeanUtils.copy(request, AppVersionControlEntity.class);
        bean.setId(IdUtil.getSnowflakeNextIdStr());
        return appVersionControlMapper.insert(bean);
    }


    @Override
    public Integer update(AppVersionControlRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("id不能为空");
        }
        AppVersionControlEntity bean = BeanUtils.copy(request, AppVersionControlEntity.class);
        return appVersionControlMapper.updateById(bean);
    }


    @Override
    public Integer deleteById(Long[] ids) {
        int resultCount = 0;
        for (Long id : ids) {
            resultCount = appVersionControlMapper.deleteById(id) + resultCount;
        }
        return resultCount;
    }

    @Override
    public AppVersionControlResponse getLastAppVersion(AppVersionControlRequest request) {
        LambdaQueryWrapper<AppVersionControlEntity> queryWrapper = Wrappers.<AppVersionControlEntity>lambdaQuery()
                .eq(AppVersionControlEntity::getAppId,request.getAppId())
                .orderByDesc(AppVersionControlEntity::getCreateTime)
                .last("limit 1");
        AppVersionControlEntity appVersionControlEntity = this.appVersionControlMapper.selectOne(queryWrapper);
        if(Objects.nonNull(appVersionControlEntity) && AppVersionCompareUtil.needUpdate(request.getVersion(), appVersionControlEntity.getVersion())){
            AppVersionControlResponse response = BeanUtils.copy(appVersionControlEntity, AppVersionControlResponse.class);
            response.setFileInfo(FileUtils.selectFileById(response.getPackageFileId()));
            return response;
        }
        return null;
    }

    /**
     * 获取最新APK 安装包
     * @return
     */
    @Override
    public AppVersionControlResponse getLastApkApp() {
        LambdaQueryWrapper<AppVersionControlEntity> queryWrapper = Wrappers.<AppVersionControlEntity>lambdaQuery()
                .eq(AppVersionControlEntity::getPackageType,2)
                .orderByDesc(AppVersionControlEntity::getCreateTime)
                .last("limit 1");
        AppVersionControlEntity appVersionControlEntity = this.appVersionControlMapper.selectOne(queryWrapper);
        if(Objects.nonNull(appVersionControlEntity)){
            AppVersionControlResponse response = BeanUtils.copy(appVersionControlEntity, AppVersionControlResponse.class);
            response.setFileInfo(FileUtils.selectFileById(response.getPackageFileId()));
            return response;
        }
        return null;
    }
}

