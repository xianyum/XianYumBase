package cn.xianyum.extension.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.extension.entity.request.EvTripRequest;
import cn.xianyum.extension.entity.response.EvTripResponse;

/**
 * @author xianyum
 * @date 2026/8/27 21:16
 */
public interface EvTripService {

    /**
     * 汇总行程
     * @return
     */
    ReturnT doSummaryEvTrip();

    /**
     * 分页查询行程
     * @param request 查询实体
     * @return 分页数据
     */
    PageResponse<EvTripResponse> getPage(EvTripRequest request);

    /**
     * 根据ID查询行程详情
     * @param id 主键
     * @return 行程详情
     */
    EvTripResponse getById(Long id);
}
