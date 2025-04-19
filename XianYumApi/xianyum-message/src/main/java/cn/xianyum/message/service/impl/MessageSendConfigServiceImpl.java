package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.dao.MessageSendConfigMapper;
import cn.xianyum.message.dao.MessageSendRelationMapper;
import cn.xianyum.message.entity.po.*;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.service.MessageSendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MessageSendConfigServiceImpl implements MessageSendConfigService {

	@Autowired
	private MessageSendConfigMapper messageSendConfigMapper;

	@Autowired
	private MessageSendRelationMapper messageSendRelationMapper;

	@Override
	public PageResponse<MessageSendConfigResponse> getPage(MessageSendConfigRequest request) {

		Page<MessageSendConfigResponse> page = new Page<>(request.getPageNum(),request.getPageSize());
		List<MessageSendConfigResponse> messageSendConfigResponses =  messageSendConfigMapper.queryList(request,page);
		return PageResponse.of(page.getTotal(),messageSendConfigResponses);
	}

	@Override
	public MessageSendConfigResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageSendConfigEntity result = messageSendConfigMapper.selectById(id);
		MessageSendConfigResponse response = BeanUtils.copy(result, MessageSendConfigResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageSendConfigRequest request) {

		MessageSendConfigEntity bean = BeanUtils.copy(request,MessageSendConfigEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		return messageSendConfigMapper.insert(bean);

	}

	@Override
	public Integer update(MessageSendConfigRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageSendConfigEntity bean = BeanUtils.copy(request,MessageSendConfigEntity.class);
		return messageSendConfigMapper.updateById(bean);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			QueryWrapper<MessageSendRelationEntity> queryWrapper = new QueryWrapper<MessageSendRelationEntity>().eq("message_send_id",id);
			messageSendRelationMapper.delete(queryWrapper);
			messageSendConfigMapper.deleteById(id);
		}
	}

	@Override
	public MessageSendConfigEntity saveOrUpdate(MessageSendConfigRequest request) {
		MessageSendConfigEntity bean = BeanUtils.copy(request,MessageSendConfigEntity.class);
		int count = 0;
		if(StringUtil.isNotEmpty(request.getId())){
			count = messageSendConfigMapper.updateById(bean);
		}else{
			bean.setId(UUIDUtils.UUIDReplace());
			count = messageSendConfigMapper.insert(bean);
		}

		if(count == 0){
			throw new SoException("保存或更新失败");
		}

		return messageSendConfigMapper.selectById(bean.getId());
	}
}
