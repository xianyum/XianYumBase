package cn.xianyum.message.service;

import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageMonitorService {

	IPage<MessageMonitorResponse> getPage(MessageMonitorRequest request);

	MessageMonitorResponse getById(String id);

	Integer save(MessageMonitorRequest request);

	Integer update(MessageMonitorRequest request);

	void deleteById(String[] ids);

	void insertMessageLog(String mId, String toUser,MessageSenderEntity messageSender, String sendResult);

	void truncate();

    Long getOnlineProxyCount();
}
