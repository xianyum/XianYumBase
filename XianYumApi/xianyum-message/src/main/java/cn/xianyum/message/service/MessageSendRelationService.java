package cn.xianyum.message.service;


import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MessageSendRelationService {

    IPage<MessageSendRelationResponse> getPage(MessageSendRelationRequest request);

    void deleteById(String id);

    Integer save(MessageSendRelationRequest request);

    Integer update(MessageSendRelationRequest request);

    MessageSendRelationResponse getById(MessageSendRelationRequest request);

    JSONArray getMessageConfigByAccountType(String messageConfigId, String messageAccountType);
}