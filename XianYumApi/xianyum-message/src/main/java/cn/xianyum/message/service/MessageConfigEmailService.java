package cn.xianyum.message.service;

import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageConfigEmailService {

	IPage<MessageConfigEmailResponse> getPage(MessageConfigEmailRequest request);

	MessageConfigEmailResponse getById(MessageConfigEmailRequest request);

	Integer save(MessageConfigEmailRequest request);

	Integer update(MessageConfigEmailRequest request);

	void deleteById(String[] ids);

	MessageConfigEmailEntity getMessageConfigWithCache(String messageConfigId);

	String getMessageConfigKey(String messageConfigId);
}
