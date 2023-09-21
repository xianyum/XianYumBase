package cn.xianyum.message.service;

import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface MessageTypeConfigService {

	IPage<MessageTypeConfigResponse> getPage(MessageTypeConfigRequest request);

	MessageTypeConfigResponse getById(String id);

	Integer save(MessageTypeConfigRequest request);

	Integer update(MessageTypeConfigRequest request);

	void deleteById(String[] ids);

    List<MessageTypeConfigResponse> getList();

	MessageTypeConfigEntity check(String messageCode);

    void updateSendCount(String messageCode);
}
