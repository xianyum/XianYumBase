package cn.xianyum.analysis.service;

import cn.xianyum.analysis.entity.po.HaoKaLotArticleEntity;
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
}
