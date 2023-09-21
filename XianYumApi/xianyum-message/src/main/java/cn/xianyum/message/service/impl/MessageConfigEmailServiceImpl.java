package cn.xianyum.message.service.impl;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.dao.MessageConfigEmailMapper;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;
import cn.xianyum.message.service.MessageConfigEmailService;
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
public class MessageConfigEmailServiceImpl implements MessageConfigEmailService {

	@Autowired
	private MessageConfigEmailMapper messageConfigEmailMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Value("${redis.message.config.prefix:xianyum-message:config:}")
	private String messageConfigPrefix;

	@Override
	public IPage<MessageConfigEmailResponse> getPage(MessageConfigEmailRequest request) {

		Page<MessageConfigEmailEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageConfigEmailEntity> queryWrapper = new QueryWrapper<MessageConfigEmailEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag())
				.like(StringUtil.isNotEmpty(request.getEmailUserName()),"email_user_name",request.getEmailUserName())
				.like(StringUtil.isNotEmpty(request.getDescription()),"description",request.getDescription())
				.orderByDesc("create_time");
		IPage<MessageConfigEmailEntity> pageResult = messageConfigEmailMapper.selectPage(page,queryWrapper);
		IPage<MessageConfigEmailResponse> responseIPage = new Page<>();
		responseIPage.setTotal(pageResult.getTotal());
		responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),MessageConfigEmailResponse.class));
		return responseIPage;
	}

	@Override
	public MessageConfigEmailResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageConfigEmailEntity result = messageConfigEmailMapper.selectById(id);
		MessageConfigEmailResponse response = BeanUtils.copy(result, MessageConfigEmailResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageConfigEmailRequest request) {

		MessageConfigEmailEntity bean = BeanUtils.copy(request,MessageConfigEmailEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		LoginUser loginUser = SecurityUtils.getLoginUser();
		bean.setCreateUserId(loginUser.getId());
		bean.setCreateUserName(loginUser.getUsername());
		return messageConfigEmailMapper.insert(bean);

	}

	@Override
	public Integer update(MessageConfigEmailRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		MessageConfigEmailEntity bean = BeanUtils.copy(request,MessageConfigEmailEntity.class);
		int count = messageConfigEmailMapper.updateById(bean);
		redisUtils.del(this.getMessageConfigKey(bean.getId()));
		return count;
	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			MessageConfigEmailEntity updateBean = new MessageConfigEmailEntity();
			updateBean.setId(id);
			updateBean.setDelTag(DeleteTagEnum.deleted.getDeleteTag());
			messageConfigEmailMapper.updateById(updateBean);
			redisUtils.del(this.getMessageConfigKey(id));
		}
	}

	@Override
	public MessageConfigEmailEntity getMessageConfigWithCache(String messageConfigId) {
		QueryWrapper<MessageConfigEmailEntity> queryWrapper = new QueryWrapper<MessageConfigEmailEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag())
				.eq("id",messageConfigId);

		String redisKey = this.getMessageConfigKey(messageConfigId);
		if(redisUtils.hasKey(redisKey)){
			String redisContent = redisUtils.getString(redisKey);
			return JSONObject.parseObject(redisContent,MessageConfigEmailEntity.class);
		}
		MessageConfigEmailEntity messageConfigEmailEntity = messageConfigEmailMapper.selectOne(queryWrapper);
		redisUtils.set(redisKey,JSONObject.toJSONString(messageConfigEmailEntity));
		return messageConfigEmailEntity;
	}


	@Override
	public String getMessageConfigKey(String messageConfigId) {
		String redisPrefix = messageConfigPrefix+messageConfigId;
		return redisPrefix;
	}

}
