package cn.xianyum.analysis.service;


import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import cn.xianyum.analysis.entity.response.XiaoDaoResponse;
import cn.xianyum.common.entity.base.PageResponse;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
public interface XiaoDaoService {

    void push();

    PageResponse<XiaoDaoResponse> getPage(XiaoDaoRequest request);
}
