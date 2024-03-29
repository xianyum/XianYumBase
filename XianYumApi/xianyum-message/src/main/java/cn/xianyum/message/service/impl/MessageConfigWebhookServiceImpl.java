package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.dao.MessageConfigWebhookMapper;
import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.request.MessageConfigWebhookRequest;
import cn.xianyum.message.entity.response.MessageConfigWebhookResponse;
import cn.xianyum.message.service.MessageConfigWebhookService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
@Slf4j
public class MessageConfigWebhookServiceImpl implements MessageConfigWebhookService {

	@Autowired
	private MessageConfigWebhookMapper messageConfigWebhookMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Value("${redis.message.config.prefix:xianyum-message:config:}")
	private String messageConfigPrefix;


	@Override
	public PageResponse<MessageConfigWebhookResponse> getPage(MessageConfigWebhookRequest request) {

		Page<MessageConfigWebhookEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageConfigWebhookEntity> queryWrapper = new QueryWrapper<MessageConfigWebhookEntity>()
				.eq("del_tag", YesOrNoEnum.YES.getStatus())
				.eq(StringUtil.isNotEmpty(request.getMessageAccountType()),"message_account_type",request.getMessageAccountType())
				.like(StringUtil.isNotEmpty(request.getDescription()),"description",request.getDescription())
				.orderByDesc("create_time");
		IPage<MessageConfigWebhookEntity> pageResult = messageConfigWebhookMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,MessageConfigWebhookResponse.class);
	}

	@Override
	public MessageConfigWebhookResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageConfigWebhookEntity result = messageConfigWebhookMapper.selectById(id);
		MessageConfigWebhookResponse response = BeanUtils.copy(result, MessageConfigWebhookResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageConfigWebhookRequest request) {

		MessageConfigWebhookEntity bean = BeanUtils.copy(request,MessageConfigWebhookEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		return messageConfigWebhookMapper.insert(bean);

	}

	@Override
	public Integer update(MessageConfigWebhookRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageConfigWebhookEntity bean = BeanUtils.copy(request,MessageConfigWebhookEntity.class);
		int count = messageConfigWebhookMapper.updateById(bean);
		redisUtils.del(this.getMessageConfigKey(bean.getId()));
		return count;
	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			MessageConfigWebhookEntity updateBean = new MessageConfigWebhookEntity();
			updateBean.setId(id);
			updateBean.setDelTag(YesOrNoEnum.NO.getStatus());
			messageConfigWebhookMapper.updateById(updateBean);
			redisUtils.del(this.getMessageConfigKey(id));
		}
	}

	@Override
	public MessageConfigWebhookEntity getMessageConfigWithCache(String messageConfigId) {
		QueryWrapper<MessageConfigWebhookEntity> queryWrapper = new QueryWrapper<MessageConfigWebhookEntity>()
				.eq("del_tag",YesOrNoEnum.YES.getStatus()).eq("id",messageConfigId);
		String redisKey = this.getMessageConfigKey(messageConfigId);
		if(redisUtils.hasKey(redisKey)){
			String redisContent = redisUtils.getString(redisKey);
			return JSONObject.parseObject(redisContent, MessageConfigWebhookEntity.class);
		}
		MessageConfigWebhookEntity messageConfigWebhookEntity = messageConfigWebhookMapper.selectOne(queryWrapper);
		redisUtils.set(redisKey,JSONObject.toJSONString(messageConfigWebhookEntity));
		return messageConfigWebhookEntity;
	}

	@Override
	public String getMessageConfigKey(String messageConfigId) {
		String redisPrefix = messageConfigPrefix+messageConfigId;
		return redisPrefix;
	}

}
