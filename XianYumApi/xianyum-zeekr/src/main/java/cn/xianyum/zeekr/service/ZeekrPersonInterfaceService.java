package cn.xianyum.zeekr.service;

import cn.xianyum.zeekr.entity.po.ZeekrInterfaceEntity;
import cn.xianyum.zeekr.entity.po.ZeekrPersonEntity;
import cn.xianyum.zeekr.entity.po.ZeekrPersonResult;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author zhangwei
 * @date 2022/8/7 14:23
 */
public interface ZeekrPersonInterfaceService {

    String getToken(String loginName);

    ZeekrPersonResult getZeekrByDay(String token);

    String executeNowDayZeekr(String token,ZeekrPersonResult zeekrPersonResult);

    void setEmployeeInfo(ZeekrPersonEntity zeekrPersonEntity);

    ZeekrInterfaceEntity getZeekrInterfaceConfig();

    JSONObject getZeekrProjectList(String loginName);

    List<ZeekrPersonResult> getZeekrByMonth(String loginName);
}
