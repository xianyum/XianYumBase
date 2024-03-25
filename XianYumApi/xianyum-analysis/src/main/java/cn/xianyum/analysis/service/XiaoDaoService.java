package cn.xianyum.analysis.service;


import cn.xianyum.analysis.entity.po.XiaoDaoAnalysisEntity;
import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import cn.xianyum.analysis.entity.response.XiaoDaoResponse;
import cn.xianyum.common.entity.base.PageResponse;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
public interface XiaoDaoService {

    void push(List<XiaoDaoEntity> pushInfo);

    PageResponse<XiaoDaoResponse> getPage(XiaoDaoRequest request);

    void xxlCrawlerParse(XiaoDaoAnalysisEntity xiaoDaoAnalysisEntity);
}
