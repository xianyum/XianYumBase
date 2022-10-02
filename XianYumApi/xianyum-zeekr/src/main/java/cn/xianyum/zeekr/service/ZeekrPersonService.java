package cn.xianyum.zeekr.service;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.zeekr.entity.request.ZeekrPersonRequest;
import cn.xianyum.zeekr.entity.response.ZeekrPersonResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface ZeekrPersonService {

	IPage<ZeekrPersonResponse> getPage(ZeekrPersonRequest request);

	ZeekrPersonResponse getById(ZeekrPersonRequest request);

	Integer save(ZeekrPersonRequest request);

	Integer update(ZeekrPersonRequest request);

	void deleteById(Long[] ids);

    ReturnT executeZeekr(Map<String, String> jobMapParams, SchedulerTool tool);
}
