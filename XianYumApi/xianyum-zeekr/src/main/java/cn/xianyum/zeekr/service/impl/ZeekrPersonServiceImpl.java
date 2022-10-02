package cn.xianyum.zeekr.service.impl;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.zeekr.dao.ZeekrPersonMapper;
import cn.xianyum.zeekr.entity.po.ZeekrPersonEntity;
import cn.xianyum.zeekr.entity.po.ZeekrPersonResult;
import cn.xianyum.zeekr.entity.request.ZeekrPersonRequest;
import cn.xianyum.zeekr.entity.response.ZeekrPersonResponse;
import cn.xianyum.zeekr.service.ZeekrPersonInterfaceService;
import cn.xianyum.zeekr.service.ZeekrPersonService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
@Slf4j
public class ZeekrPersonServiceImpl implements ZeekrPersonService {

	@Autowired
	private ZeekrPersonMapper zeekrPersonMapper;

	@Autowired
	private ZeekrPersonInterfaceService zeekrPersonInterfaceService;

	@Autowired
	private MessageSender messageSender;

	@Override
	public IPage<ZeekrPersonResponse> getPage(ZeekrPersonRequest request) {
		String loginName = request.getLoginName();
		String employeeName = request.getEmployeeName();

		Page<ZeekrPersonEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<ZeekrPersonEntity> queryWrapper = new QueryWrapper<ZeekrPersonEntity>()
				.like(StringUtil.isNotEmpty(loginName),"login_name",loginName)
				.like(StringUtil.isNotEmpty(employeeName),"employee_name",employeeName)
				.orderByDesc("create_time");
		IPage<ZeekrPersonEntity> pageResult = zeekrPersonMapper.selectPage(page,queryWrapper);
		IPage<ZeekrPersonResponse> responseIPage = new Page<>();
		responseIPage.setTotal(pageResult.getTotal());
		responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),ZeekrPersonResponse.class));
		return responseIPage;
	}

	@Override
	public ZeekrPersonResponse getById(ZeekrPersonRequest request) {

		if(null == request.getId()){
			throw new SoException("id不能为空");
		}
		ZeekrPersonEntity result = zeekrPersonMapper.selectById(request.getId());
		ZeekrPersonResponse response = BeanUtils.copy(result, ZeekrPersonResponse.class);
		return response;

	}

	@Override
	public Integer save(ZeekrPersonRequest request) {

		ZeekrPersonEntity bean = BeanUtils.copy(request,ZeekrPersonEntity.class);
		bean.setCreateTime(new Date());
		bean.setCreateUserId(SecurityUtils.getLoginUser().getId());
		zeekrPersonInterfaceService.setEmployeeInfo(bean);
		return zeekrPersonMapper.insert(bean);

	}

	@Override
	public Integer update(ZeekrPersonRequest request) {

		if(null == request.getId()){
			throw new SoException("id不能为空");
		}
		ZeekrPersonEntity bean = BeanUtils.copy(request,ZeekrPersonEntity.class);
		return zeekrPersonMapper.updateById(bean);

	}

	@Override
	public void deleteById(Long[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (Long id : ids){
			zeekrPersonMapper.deleteById(id);
		}
	}

	@Override
	public ReturnT executeZeekr(Map<String, String> jobMapParams, SchedulerTool tool) {
		QueryWrapper<ZeekrPersonEntity> queryWrapper = new QueryWrapper<ZeekrPersonEntity>();
		List<ZeekrPersonEntity> zeekrPersonEntities = zeekrPersonMapper.selectList(queryWrapper);
		for(ZeekrPersonEntity zeekrPersonEntity : zeekrPersonEntities){
			String token  = this.zeekrPersonInterfaceService.getToken(zeekrPersonEntity.getLoginName());
			ZeekrPersonResult zeekrByDay = this.zeekrPersonInterfaceService.getZeekrByDay(token);
			if(null == zeekrByDay.getApprovalStatus() || zeekrByDay.getApprovalStatus() == 0){
				zeekrByDay.setProjectId(Long.valueOf(zeekrPersonEntity.getProjectId()));
				zeekrByDay.setProjectManagerId(Long.valueOf(zeekrPersonEntity.getProjectManagerId()));
				zeekrByDay.setProjectManagerName(zeekrPersonEntity.getProjectManagerName());
				zeekrByDay.setProjectName(zeekrPersonEntity.getProjectName());
				zeekrByDay.setTsEmployeeNum(zeekrPersonEntity.getEmployeeNum());
				zeekrByDay.setTsEmployeeName(zeekrPersonEntity.getEmployeeName());
				zeekrByDay.setTsTime(DateTime.now().toString("yyyy-MM-dd 00:00:00"));
				String result = this.zeekrPersonInterfaceService.executeNowDayZeekr(token, zeekrByDay);

				// 说明执行失败，发送消息
				if(StringUtil.isNotEmpty(result)){
					Map<String,Object> content = new LinkedHashMap<>();
					content.put("员工名称：", zeekrPersonEntity.getEmployeeName());
					content.put("错误原因：", result);
					MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
					messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
					messageSender.sendAsyncMessage(MessageCodeEnums.ZEEKR_EXECUTE_FAIL_NOTIFY.getMessageCode(),messageSenderEntity);
				}
			}
		}
		return ReturnT.SUCCESS;
	}

}
