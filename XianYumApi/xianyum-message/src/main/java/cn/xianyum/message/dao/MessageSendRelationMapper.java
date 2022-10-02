package cn.xianyum.message.dao;

import cn.xianyum.message.entity.po.MessageSendRelationEntity;
import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper
public interface MessageSendRelationMapper extends BaseMapper<MessageSendRelationEntity> {

}
