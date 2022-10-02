package cn.xianyum.message.service;

import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.request.MessageConfigWechatRequest;
import cn.xianyum.message.entity.response.MessageConfigWechatResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageConfigWechatService {

	IPage<MessageConfigWechatResponse> getPage(MessageConfigWechatRequest request);

	MessageConfigWechatResponse getById(MessageConfigWechatRequest request);

	Integer save(MessageConfigWechatRequest request);

	Integer update(MessageConfigWechatRequest request);

	void deleteById(String[] ids);

	MessageConfigWechatEntity getMessageConfigWithCache(String messageConfigId);

	String getMessageConfigKey(String messageConfigId);
}
