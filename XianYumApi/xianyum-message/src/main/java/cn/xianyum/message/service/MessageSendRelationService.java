package cn.xianyum.message.service;


import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageSendRelationService {

    PageResponse<MessageSendRelationResponse> getPage(MessageSendRelationRequest request);

    void deleteById(String id);

    Integer save(MessageSendRelationRequest request);

    Integer update(MessageSendRelationRequest request);

    MessageSendRelationResponse getById(String id);

    JSONArray getMessageConfigByAccountType(String messageConfigId, String messageAccountType);
}
