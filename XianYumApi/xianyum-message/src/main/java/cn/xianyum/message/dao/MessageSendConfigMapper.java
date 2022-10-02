package cn.xianyum.message.dao;

import cn.xianyum.message.entity.po.MessageSendConfigEntity;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageSendConfigMapper extends BaseMapper<MessageSendConfigEntity> {

    List<MessageSendConfigResponse> queryList(@Param("request") MessageSendConfigRequest request, Page<MessageSendConfigResponse> page);

    List<MessageSendConfigResponse> selectListByMessageCode(@Param("messageCode") String messageCode);

}
