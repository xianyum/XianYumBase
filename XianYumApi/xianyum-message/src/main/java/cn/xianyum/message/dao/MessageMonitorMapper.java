package cn.xianyum.message.dao;

import cn.xianyum.message.entity.po.MessageMonitorEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MessageMonitorMapper extends BaseMapper<MessageMonitorEntity> {

    @Update("truncate table message_monitor")
    void truncate();
}
