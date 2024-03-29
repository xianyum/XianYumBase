package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.dao.MessageMonitorMapper;
import cn.xianyum.message.entity.po.MessageMonitorEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;
import cn.xianyum.message.service.MessageMonitorService;
import cn.xianyum.message.service.MessageTypeConfigService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

@Service
@Slf4j
public class MessageMonitorServiceImpl implements MessageMonitorService {

	@Autowired
	private MessageMonitorMapper messageMonitorMapper;

	@Autowired
	private MessageTypeConfigService messageTypeConfigService;

	@Override
	public PageResponse<MessageMonitorResponse> getPage(MessageMonitorRequest request) {
		Page<MessageMonitorEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<MessageMonitorEntity>()
				.eq(StringUtil.isNotEmpty(request.getId()),"id",request.getId())
				.eq(StringUtil.isNotEmpty(request.getMessageType()),"message_type",request.getMessageType())
				.like(StringUtil.isNotEmpty(request.getMessageCode()),"message_code",request.getMessageCode())
				.like(StringUtil.isNotEmpty(request.getMessageTitle()),"message_title",request.getMessageTitle())
				.like(StringUtil.isNotEmpty(request.getMessageContent()),"message_content",request.getMessageContent())
				.gt(Objects.nonNull(request.getParams().get("beginTime")),"create_time",request.getParams().get("beginTime"))
				.lt(Objects.nonNull(request.getParams().get("endTime")),"create_time",request.getParams().get("endTime"))
				.orderByDesc("create_time");;
		IPage<MessageMonitorEntity> pageResult = messageMonitorMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,MessageMonitorResponse.class);
	}

	@Override
	public MessageMonitorResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageMonitorEntity result = messageMonitorMapper.selectById(id);
		MessageMonitorResponse response = BeanUtils.copy(result, MessageMonitorResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageMonitorRequest request) {

		MessageMonitorEntity bean = BeanUtils.copy(request,MessageMonitorEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		return messageMonitorMapper.insert(bean);

	}

	@Override
	public Integer update(MessageMonitorRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageMonitorEntity bean = BeanUtils.copy(request,MessageMonitorEntity.class);
		return messageMonitorMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			messageMonitorMapper.deleteById(id);
		}
	}

	@Override
	public void insertMessageLog(String mId, String toUser, MessageSenderEntity messageSender, String sendResult) {
		String messageCode = messageSender.getMessageCode();
		MessageMonitorEntity bean = new MessageMonitorEntity();
		bean.setId(mId);
		bean.setMessageResponse(sendResult);
		bean.setMessageTitle(messageSender.getTitle());
		bean.setMessageType(messageSender.getMessageAccountType());
		bean.setToUser(toUser);
		bean.setMessageCode(messageCode);
		bean.setMessageContent(JSONObject.toJSONString(messageSender));
		messageMonitorMapper.insert(bean);

		messageTypeConfigService.updateSendCount(messageCode);
	}

	@Override
	public void truncate() {
		messageMonitorMapper.truncate();
	}

	@Override
	public Long getOnlineProxyCount() {
		QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
		return messageMonitorMapper.selectCount(queryWrapper);
	}

}
