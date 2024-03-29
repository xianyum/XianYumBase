package cn.xianyum.message.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.po.MessageSendConfigEntity;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import org.thymeleaf.context.Context;

public interface MessageSendConfigService {

	PageResponse<MessageSendConfigResponse> getPage(MessageSendConfigRequest request);

	MessageSendConfigResponse getById(String id);

	Integer save(MessageSendConfigRequest request);

	Integer update(MessageSendConfigRequest request);

	void deleteById(String[] ids);

	MessageSendConfigEntity saveOrUpdate(MessageSendConfigRequest request);

    void sendMessage(String messageCode, MessageSenderEntity messageSender);

    void sendEmailTemplateMessage(String messageCode, MessageSenderEntity messageSender, Context context);

	boolean verifySendRules(MessageSendConfigResponse messageSendConfigResponse);
}
