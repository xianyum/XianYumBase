package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.dao.MessageConfigEmailMapper;
import cn.xianyum.message.dao.MessageConfigWebhookMapper;
import cn.xianyum.message.dao.MessageConfigWechatMapper;
import cn.xianyum.message.dao.MessageSendRelationMapper;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.po.MessageSendRelationEntity;
import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.service.MessageSendRelationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Service
@Slf4j
public class MessageSendRelationServiceImpl implements MessageSendRelationService {

	@Autowired
	private MessageSendRelationMapper messageSendRelationMapper;

	@Autowired
	private MessageConfigWechatMapper messageConfigWechatMapper;

	@Autowired
	private MessageConfigWebhookMapper MessageConfigWebhookMapper;

	@Autowired
	private MessageConfigEmailMapper MessageConfigEmailMapper;


	@Override
	public PageResponse<MessageSendRelationResponse> getPage(MessageSendRelationRequest request) {
		Page<MessageSendRelationEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageSendRelationEntity> queryWrapper = new QueryWrapper<MessageSendRelationEntity>()
				.eq("message_send_id",request.getMessageSendId())
				.eq(StringUtil.isNotEmpty(request.getMessageAccountType()),"message_account_type",request.getMessageAccountType())
				.orderByDesc("create_time");
		IPage<MessageSendRelationEntity> pageResult = messageSendRelationMapper.selectPage(page,queryWrapper);
		List<MessageSendRelationResponse> messageSendRelationResponses = BeanUtils.copyList(pageResult.getRecords(), MessageSendRelationResponse.class);
		if(!CollectionUtils.isEmpty(pageResult.getRecords())){
			for(MessageSendRelationResponse item : messageSendRelationResponses){
				JSONArray messageConfigByAccountType = this.getMessageConfigByAccountType(item.getMessageConfigId(), item.getMessageAccountType());
				if(messageConfigByAccountType != null){
					item.setMessageConfigDescription(messageConfigByAccountType.getJSONObject(0).getString("description"));
				}
			}
		}
		return PageResponse.of(pageResult.getTotal(),messageSendRelationResponses);
	}

	@Override
	public void deleteById(String id) {
		messageSendRelationMapper.deleteById(id);
	}

	@Override
	public Integer save(MessageSendRelationRequest request) {
		MessageSendRelationEntity bean = BeanUtils.copy(request,MessageSendRelationEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		return messageSendRelationMapper.insert(bean);
	}

	@Override
	public Integer update(MessageSendRelationRequest request) {
		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageSendRelationEntity bean = new MessageSendRelationEntity();
		bean.setId(request.getId());
		bean.setToUser(request.getToUser());
		return messageSendRelationMapper.updateById(bean);

	}

	@Override
	public MessageSendRelationResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageSendRelationEntity result = messageSendRelationMapper.selectById(id);
		MessageSendRelationResponse response = BeanUtils.copy(result, MessageSendRelationResponse.class);
		return response;

	}

	@Override
	public JSONArray getMessageConfigByAccountType(String messageConfigId, String messageAccountType) {
		if(StringUtil.isEmpty(messageAccountType)){
			throw new SoException("账户类型不能为空");
		}
		boolean idNotEmpty = StringUtil.isNotEmpty(messageConfigId);

		String objJson = "";
		switch (MessageAccountTypeEnums.getByCode(messageAccountType)){
			case WECHAT:
				QueryWrapper<MessageConfigWechatEntity> messageConfigWechatEntityQueryWrapper = new QueryWrapper<MessageConfigWechatEntity>()
						.eq("del_tag", DeleteTagEnum.Delete.getDeleteTag())
						.eq(idNotEmpty,"id",messageConfigId);
				List<MessageConfigWechatEntity> messageConfigWechatEntities = messageConfigWechatMapper.selectList(messageConfigWechatEntityQueryWrapper);
				objJson = JSONObject.toJSONString(messageConfigWechatEntities);
				break;
			case EMAIL:
				QueryWrapper<MessageConfigEmailEntity> messageConfigEmailEntityQueryWrapper = new QueryWrapper<MessageConfigEmailEntity>()
						.eq("del_tag", DeleteTagEnum.Delete.getDeleteTag())
						.eq(idNotEmpty,"id",messageConfigId);
				List<MessageConfigEmailEntity> messageConfigEmailEntities = MessageConfigEmailMapper.selectList(messageConfigEmailEntityQueryWrapper);
				objJson = JSONObject.toJSONString(messageConfigEmailEntities);
				break;
			case DD_WEBHOOK:
			case FS_WEBHOOK:
			case CUSTOM_WEBHOOK:
				QueryWrapper<MessageConfigWebhookEntity> messageConfigWebhookEntityQueryWrapper = new QueryWrapper<MessageConfigWebhookEntity>()
						.eq("del_tag", DeleteTagEnum.Delete.getDeleteTag())
						.eq(idNotEmpty,"id",messageConfigId)
						.eq("message_account_type",messageAccountType);
				List<MessageConfigWebhookEntity> messageConfigWebhookEntities = MessageConfigWebhookMapper.selectList(messageConfigWebhookEntityQueryWrapper);
				objJson = JSONObject.toJSONString(messageConfigWebhookEntities);
				break;
		}
		return JSONObject.parseArray(objJson);
	}

}
