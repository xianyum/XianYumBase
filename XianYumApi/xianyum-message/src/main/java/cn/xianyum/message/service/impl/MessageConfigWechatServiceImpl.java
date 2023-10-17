package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.dao.MessageConfigWechatMapper;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.request.MessageConfigWechatRequest;
import cn.xianyum.message.entity.response.MessageConfigWechatResponse;
import cn.xianyum.message.service.MessageConfigWechatService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
@Slf4j
public class MessageConfigWechatServiceImpl implements MessageConfigWechatService {

	@Autowired
	private MessageConfigWechatMapper messageConfigWechatMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Value("${redis.message.config.prefix:xianyum-message:config:}")
	private String messageConfigPrefix;

	@Override
	public PageResponse<MessageConfigWechatResponse> getPage(MessageConfigWechatRequest request) {

		Page<MessageConfigWechatEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageConfigWechatEntity> queryWrapper = new QueryWrapper<MessageConfigWechatEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag())
				.like(StringUtil.isNotEmpty(request.getDescription()),"description",request.getDescription())
				.orderByDesc("create_time");
		IPage<MessageConfigWechatEntity> pageResult = messageConfigWechatMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,MessageConfigWechatResponse.class);
	}

	@Override
	public MessageConfigWechatResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageConfigWechatEntity result = messageConfigWechatMapper.selectById(id);
		MessageConfigWechatResponse response = BeanUtils.copy(result, MessageConfigWechatResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageConfigWechatRequest request) {

		MessageConfigWechatEntity bean = BeanUtils.copy(request,MessageConfigWechatEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		return messageConfigWechatMapper.insert(bean);

	}

	@Override
	public Integer update(MessageConfigWechatRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageConfigWechatEntity bean = BeanUtils.copy(request,MessageConfigWechatEntity.class);
		int count = messageConfigWechatMapper.updateById(bean);
		redisUtils.del(this.getMessageConfigKey(bean.getId()));
		return count;

	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			MessageConfigWechatEntity updateBean = new MessageConfigWechatEntity();
			updateBean.setId(id);
			updateBean.setDelTag(DeleteTagEnum.deleted.getDeleteTag());
			messageConfigWechatMapper.updateById(updateBean);
			redisUtils.del(this.getMessageConfigKey(id));
		}
	}

	@Override
	public MessageConfigWechatEntity getMessageConfigWithCache(String messageConfigId) {
		QueryWrapper<MessageConfigWechatEntity> queryWrapper = new QueryWrapper<MessageConfigWechatEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag()).eq("id",messageConfigId);
		String redisKey = this.getMessageConfigKey(messageConfigId);
		if(redisUtils.hasKey(redisKey)){
			String redisContent = redisUtils.getString(redisKey);
			return JSONObject.parseObject(redisContent, MessageConfigWechatEntity.class);
		}
		MessageConfigWechatEntity messageConfigWechatEntity = messageConfigWechatMapper.selectOne(queryWrapper);
		redisUtils.set(redisKey,JSONObject.toJSONString(messageConfigWechatEntity));
		return messageConfigWechatEntity;
	}

	@Override
	public String getMessageConfigKey(String messageConfigId) {
		String redisPrefix = messageConfigPrefix+messageConfigId;
		return redisPrefix;
	}

}
