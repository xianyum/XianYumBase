package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.po.HaoKaLotArticleEntity;
import cn.xianyum.extension.entity.request.HaoKaLotProductRequest;
import cn.xianyum.extension.entity.response.HaoKaLotProductResponse;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2023/10/6 18:28
 */
public interface HaoKaLotService {

    ReturnT pushArticleMessage(Map<String, String> jobMapParams, SchedulerTool tool);

    /**
     * 获取172号卡登录token
     * @return
     */
    String getAccessTokenByLogin();

    /**
     * 获取172号通知
     * @return
     */
    List<HaoKaLotArticleEntity> getHaoKaLotArticleList();

    /**
     * 分页获取172号卡商品列表
     * @param request
     * @return
     */
    PageResponse<HaoKaLotProductResponse> getPage(HaoKaLotProductRequest request);
}
