package cn.xianyum.message.dao;

import cn.xianyum.message.entity.po.MessageTypeConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageTypeConfigMapper extends BaseMapper<MessageTypeConfigEntity> {

    void updateSendCount(@Param("messageCode") String messageCode);
}
