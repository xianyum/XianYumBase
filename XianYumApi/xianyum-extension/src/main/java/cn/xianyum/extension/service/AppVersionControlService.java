package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.request.AppVersionControlRequest;
import cn.xianyum.extension.entity.response.AppVersionControlResponse;
import cn.xianyum.common.entity.base.PageResponse;

/**
 * APP版本管理(AppVersionControl)service层
 *
 * @author zhangwei
 * @since 2026-01-23 17:28:11
 */
public interface AppVersionControlService {

    PageResponse<AppVersionControlResponse> getPage(AppVersionControlRequest request);

    AppVersionControlResponse getById(Long id);

    Integer save(AppVersionControlRequest request);

    Integer update(AppVersionControlRequest request);

    Integer deleteById(Long[] ids);

    AppVersionControlResponse getLastAppVersion(AppVersionControlRequest request);

    AppVersionControlResponse getLastApkApp();
}
