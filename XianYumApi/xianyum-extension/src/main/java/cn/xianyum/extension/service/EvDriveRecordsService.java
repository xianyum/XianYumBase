package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.common.entity.base.PageResponse;

/**
 * 新能源车行驶记录(EvDriveRecords)service层
 *
 * @author makejava
 * @since 2025-03-06 20:43:40
 */
public interface EvDriveRecordsService {

    PageResponse<EvDriveRecordsResponse> getPage(EvDriveRecordsRequest request);

    EvDriveRecordsResponse getById(Long id);

    Integer save(EvDriveRecordsRequest request);

    Integer update(EvDriveRecordsRequest request);

    Integer deleteById(Long[] ids);

    /**
     * 校验重复数据
     * @param request
     */
    void checkForDuplicateData(EvDriveRecordsRequest request);
}
