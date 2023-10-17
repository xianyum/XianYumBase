package cn.xianyum.message.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;


public interface MessageConfigEmailService {

	PageResponse<MessageConfigEmailResponse> getPage(MessageConfigEmailRequest request);

	MessageConfigEmailResponse getById(String id);

	Integer save(MessageConfigEmailRequest request);

	Integer update(MessageConfigEmailRequest request);

	void deleteById(String[] ids);

	MessageConfigEmailEntity getMessageConfigWithCache(String messageConfigId);

	String getMessageConfigKey(String messageConfigId);
}
