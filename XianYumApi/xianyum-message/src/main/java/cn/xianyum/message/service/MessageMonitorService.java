package cn.xianyum.message.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;

public interface MessageMonitorService {

	PageResponse<MessageMonitorResponse> getPage(MessageMonitorRequest request);

	MessageMonitorResponse getById(String id);

	Integer save(MessageMonitorRequest request);

	Integer update(MessageMonitorRequest request);

	void deleteById(String[] ids);

	void insertMessageLog(String mId, String toUser,MessageSenderEntity messageSender, String sendResult);

	void truncate();

    Long getOnlineProxyCount();
}
