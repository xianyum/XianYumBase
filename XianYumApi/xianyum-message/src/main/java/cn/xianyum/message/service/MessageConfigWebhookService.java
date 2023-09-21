package cn.xianyum.message.service;

import cn.xianyum.message.entity.po.MessageConfigWebhookEntity;
import cn.xianyum.message.entity.request.MessageConfigWebhookRequest;
import cn.xianyum.message.entity.response.MessageConfigWebhookResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageConfigWebhookService {

	IPage<MessageConfigWebhookResponse> getPage(MessageConfigWebhookRequest request);

	MessageConfigWebhookResponse getById(String id);

	Integer save(MessageConfigWebhookRequest request);

	Integer update(MessageConfigWebhookRequest request);

	void deleteById(String[] ids);

    MessageConfigWebhookEntity getMessageConfigWithCache(String messageConfigId);

	String getMessageConfigKey(String messageConfigId);
}
