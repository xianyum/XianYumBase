package cn.xianyum.message.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.po.MessageConfigWechatEntity;
import cn.xianyum.message.entity.request.MessageConfigWechatRequest;
import cn.xianyum.message.entity.response.MessageConfigWechatResponse;

public interface MessageConfigWechatService {

	PageResponse<MessageConfigWechatResponse> getPage(MessageConfigWechatRequest request);

	MessageConfigWechatResponse getById(String id);

	Integer save(MessageConfigWechatRequest request);

	Integer update(MessageConfigWechatRequest request);

	void deleteById(String[] ids);

	MessageConfigWechatEntity getMessageConfigWithCache(String messageConfigId);

	String getMessageConfigKey(String messageConfigId);
}
