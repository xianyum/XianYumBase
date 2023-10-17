package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.dao.MessageSendConfigMapper;
import cn.xianyum.message.dao.MessageSendRelationMapper;
import cn.xianyum.message.entity.po.*;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import cn.xianyum.message.infra.sender.EmailSender;
import cn.xianyum.message.infra.sender.WebhookSender;
import cn.xianyum.message.infra.sender.WechatSender;
import cn.xianyum.message.service.MessageSendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class MessageSendConfigServiceImpl implements MessageSendConfigService {

	@Autowired
	private MessageSendConfigMapper messageSendConfigMapper;

	@Autowired
	private MessageSendRelationMapper messageSendRelationMapper;

	@Autowired
	private WebhookSender webhookSender;

	@Autowired
	private WechatSender wechatSender;

	@Autowired
	private EmailSender emailSender;


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

	@Override
	public void sendMessage(String messageCode, MessageSenderEntity messageSender) {

		List<MessageSendConfigResponse> messageSendConfigResponseList = messageSendConfigMapper.selectListByMessageCode(messageCode);
		if(!CollectionUtils.isEmpty(messageSendConfigResponseList)) {
			for(MessageSendConfigResponse messageSendConfigResponse:messageSendConfigResponseList) {
				// 校验发信规则
				if(this.verifySendRules(messageSendConfigResponse)){
					continue;
				}

				if(StringUtil.isEmpty(messageSender.getTitle())){
					messageSender.setTitle(messageSendConfigResponse.getMessageTitle());
				}

				messageSender.setMessageCode(messageSendConfigResponse.getMessageCode());
				for(MessageSendRelationResponse item : messageSendConfigResponse.getMessageSendRelationResponses()){
					messageSender.setMessageConfigId(item.getMessageConfigId());
					messageSender.setMessageAccountType(item.getMessageAccountType());
					// 发送消息
					switch (MessageAccountTypeEnums.getByCode(item.getMessageAccountType())){
						case WECHAT:
							if(StringUtil.isNotEmpty(item.getToUser())){
								messageSender.setWechatToUser(item.getToUser());
							}
							wechatSender.sendMessage(messageSender);
							break;
						case EMAIL:
							if(StringUtil.isNotEmpty(item.getToUser())){
								messageSender.setEmailToUser(item.getToUser());
							}
							emailSender.sendMessage(messageSender);
							break;
						case DD_WEBHOOK:
							webhookSender.sendDdMessage(messageSender);
							break;
						case FS_WEBHOOK:
							webhookSender.sendFsMessage(messageSender);
							break;
						case CUSTOM_WEBHOOK:
							webhookSender.sendCustomMessage(messageSender);
							break;
					}
				}
			}
		}
	}


	@Override
	public void sendEmailTemplateMessage(String messageCode, MessageSenderEntity messageSender, Context context) {
		List<MessageSendConfigResponse> messageSendRelationResponses = messageSendConfigMapper.selectListByMessageCode(messageCode);
		if(!CollectionUtils.isEmpty(messageSendRelationResponses)) {
			for(MessageSendConfigResponse messageSendConfigResponse:messageSendRelationResponses){
				// 校验发信规则
				if(this.verifySendRules(messageSendConfigResponse)){
					continue;
				}

				if(StringUtil.isEmpty(messageSender.getTitle())){
					messageSender.setTitle(messageSendConfigResponse.getMessageTitle());
				}

				messageSender.setMessageCode(messageSendConfigResponse.getMessageCode());
				for (MessageSendRelationResponse item : messageSendConfigResponse.getMessageSendRelationResponses()) {
					messageSender.setMessageConfigId(item.getMessageConfigId());
					messageSender.setMessageAccountType(item.getMessageAccountType());
					// 发送邮箱消息
					switch (MessageAccountTypeEnums.getByCode(item.getMessageAccountType())){
						case EMAIL:
							if(StringUtil.isNotEmpty(item.getToUser())){
								messageSender.setEmailToUser(item.getToUser());
							}
							emailSender.sendEmailTemplateMessage(messageSender,context);
							break;
					}
				}
			}
		}
	}

	/**
	 * true：校验不通过 false：校验通过
	 * @param messageSendConfigResponse
	 * @return
	 */
	@Override
	public boolean verifySendRules(MessageSendConfigResponse messageSendConfigResponse) {

		if(messageSendConfigResponse == null){
			return true;
		}

		if(null != messageSendConfigResponse.getLimitSendStartTime() && null != messageSendConfigResponse.getLimitSendEndTime()){
			Date limitSendStartTime = messageSendConfigResponse.getLimitSendStartTime();
			Date limitSendEndTime = messageSendConfigResponse.getLimitSendEndTime();
			if(DateUtils.checkNowBetweenTime(limitSendStartTime,limitSendEndTime)){
				return true;
			}
		}
		return false;
	}


}
