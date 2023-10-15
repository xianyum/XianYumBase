package cn.xianyum.message.service.impl;

import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.message.dao.MessageTypeConfigMapper;
import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import cn.xianyum.message.service.MessageTypeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@Service
@Slf4j
public class MessageTypeConfigServiceImpl implements MessageTypeConfigService {

	@Autowired
	private MessageTypeConfigMapper messageTypeConfigMapper;

	@Override
	public IPage<MessageTypeConfigResponse> getPage(MessageTypeConfigRequest request) {

		Page<MessageTypeConfigEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<MessageTypeConfigEntity> queryWrapper = new QueryWrapper<MessageTypeConfigEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag())
				.like(StringUtil.isNotEmpty(request.getMessageCode()),"message_code",request.getMessageCode())
				.like(StringUtil.isNotEmpty(request.getDescription()),"description",request.getDescription())
				.orderByDesc("create_time");
		IPage<MessageTypeConfigEntity> pageResult = messageTypeConfigMapper.selectPage(page,queryWrapper);
		IPage<MessageTypeConfigResponse> responseIPage = new Page<>();
		responseIPage.setTotal(pageResult.getTotal());
		responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),MessageTypeConfigResponse.class));
		return responseIPage;
	}

	@Override
	public MessageTypeConfigResponse getById(String id) {

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		MessageTypeConfigEntity result = messageTypeConfigMapper.selectById(id);
		MessageTypeConfigResponse response = BeanUtils.copy(result, MessageTypeConfigResponse.class);
		return response;

	}

	@Override
	public Integer save(MessageTypeConfigRequest request) {

		MessageTypeConfigEntity bean = BeanUtils.copy(request,MessageTypeConfigEntity.class);

		// 判断是否重复
		QueryWrapper<MessageTypeConfigEntity> queryWrapper = new QueryWrapper<MessageTypeConfigEntity>()
				.eq("message_code",bean.getMessageCode()).eq("del_tag",DeleteTagEnum.Delete.getDeleteTag());
		Long repeatCount = messageTypeConfigMapper.selectCount(queryWrapper);
		if(repeatCount > 0){
			throw new SoException("消息编码重复");
		}

		bean.setId(UUIDUtils.UUIDReplace());
		return messageTypeConfigMapper.insert(bean);
	}

	@Override
	public Integer update(MessageTypeConfigRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}

		MessageTypeConfigEntity bean = new MessageTypeConfigEntity();
		bean.setDescription(request.getDescription());
		bean.setId(request.getId());
		bean.setEchartsTag(request.getEchartsTag());
		return messageTypeConfigMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			MessageTypeConfigEntity updateBean = new MessageTypeConfigEntity();
			updateBean.setId(id);
			updateBean.setDelTag(DeleteTagEnum.deleted.getDeleteTag());
			messageTypeConfigMapper.updateById(updateBean);
		}
	}

	@Override
	public List<MessageTypeConfigResponse> getList() {
		QueryWrapper<MessageTypeConfigEntity> queryWrapper = new QueryWrapper<MessageTypeConfigEntity>()
				.eq("del_tag",DeleteTagEnum.Delete.getDeleteTag());
		List<MessageTypeConfigEntity> messageTypeConfigEntities = messageTypeConfigMapper.selectList(queryWrapper);
		List<MessageTypeConfigResponse> responseList = BeanUtils.copyList(messageTypeConfigEntities, MessageTypeConfigResponse.class);
		return responseList;
	}

	@Override
	public MessageTypeConfigEntity check(String messageCode) {
		if(StringUtil.isEmpty(messageCode)){
			throw new SoException("消息编码参数未找到");
		}
		QueryWrapper<MessageTypeConfigEntity> queryWrapper = new QueryWrapper<MessageTypeConfigEntity>()
				.eq("message_code",messageCode).eq("del_tag",DeleteTagEnum.Delete.getDeleteTag());
		MessageTypeConfigEntity messageTypeConfigEntity = messageTypeConfigMapper.selectOne(queryWrapper);
		if(messageTypeConfigEntity == null){
			throw new SoException("未找到此消息编码："+messageCode);
		}
		return messageTypeConfigEntity;
	}

	@Override
	public void updateSendCount(String messageCode) {
		messageTypeConfigMapper.updateSendCount(messageCode);
	}

}
