package cn.xianyum.sheduler.dao;

import cn.xianyum.sheduler.entity.po.JobLogEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface JobLogMapper extends BaseMapper<JobLogEntity> {

    void truncateLog();
}
