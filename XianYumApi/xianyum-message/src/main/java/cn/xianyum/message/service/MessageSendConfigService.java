package cn.xianyum.message.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.po.MessageSendConfigEntity;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;

public interface MessageSendConfigService {

	PageResponse<MessageSendConfigResponse> getPage(MessageSendConfigRequest request);

	MessageSendConfigResponse getById(String id);

	Integer save(MessageSendConfigRequest request);

	Integer update(MessageSendConfigRequest request);

	void deleteById(String[] ids);

	MessageSendConfigEntity saveOrUpdate(MessageSendConfigRequest request);
}
