package cn.xianyum.extension.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.AppVersionCompareUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.xianyum.common.utils.FileServiceUtils;
import cn.xianyum.common.utils.FileUtils;
import cn.hutool.core.util.StrUtil;
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

import java.util.Arrays;
import java.util.List;
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
                .eq(StrUtil.isNotBlank(request.getAppId()),AppVersionControlEntity::getAppId,request.getAppId())
                .orderByDesc(AppVersionControlEntity::getCreateTime);
        Page<AppVersionControlEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<AppVersionControlEntity> pageResult = appVersionControlMapper.selectPage(page, queryWrapper);
        return PageResponse.of(pageResult, AppVersionControlResponse.class);

    }


    @Override
    public AppVersionControlResponse getById(Long id) {
        AppVersionControlEntity result = appVersionControlMapper.selectById(id);
        AppVersionControlResponse response = BeanUtil.toBean(result, AppVersionControlResponse.class);
        if(Objects.nonNull(response)){
            response.setFileInfo(FileUtils.selectFileById(response.getPackageFileId()));
        }
        return response;
    }


    @Override
    public Integer save(AppVersionControlRequest request) {
        AppVersionControlEntity bean = BeanUtil.toBean(request, AppVersionControlEntity.class);
        bean.setId(IdUtil.getSnowflakeNextIdStr());
        FileServiceUtils.transferPermanent(List.of(bean.getPackageFileId()));
        return appVersionControlMapper.insert(bean);
    }


    @Override
    public Integer update(AppVersionControlRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("id不能为空");
        }
        AppVersionControlEntity bean = BeanUtil.toBean(request, AppVersionControlEntity.class);
        FileServiceUtils.transferPermanent(List.of(bean.getPackageFileId()));
        return appVersionControlMapper.updateById(bean);
    }


    @Override
    public Integer deleteById(Long[] ids) {
        List<AppVersionControlEntity> appVersionControlEntityList = this.appVersionControlMapper.selectByIds(Arrays.asList(ids));
        for (AppVersionControlEntity appVersionControlEntity : appVersionControlEntityList) {
            FileServiceUtils.deleteFiles(List.of(appVersionControlEntity.getPackageFileId()));
        }
        appVersionControlMapper.deleteByIds(Arrays.asList(ids));
        return appVersionControlEntityList.size();
    }

    @Override
    public AppVersionControlResponse getLastAppVersion(AppVersionControlRequest request) {
        LambdaQueryWrapper<AppVersionControlEntity> queryWrapper = Wrappers.<AppVersionControlEntity>lambdaQuery()
                .eq(AppVersionControlEntity::getAppId,request.getAppId())
                .orderByDesc(AppVersionControlEntity::getCreateTime)
                .last("limit 1");
        AppVersionControlEntity appVersionControlEntity = this.appVersionControlMapper.selectOne(queryWrapper);
        if(Objects.nonNull(appVersionControlEntity) && AppVersionCompareUtil.needUpdate(request.getVersion(), appVersionControlEntity.getVersion())){
            AppVersionControlResponse response = BeanUtil.toBean(appVersionControlEntity, AppVersionControlResponse.class);
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
            AppVersionControlResponse response = BeanUtil.toBean(appVersionControlEntity, AppVersionControlResponse.class);
            response.setFileInfo(FileUtils.selectFileById(response.getPackageFileId()));
            return response;
        }
        return null;
    }
}
