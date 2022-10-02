package cn.xianyum.sheduler.dao;

import cn.xianyum.sheduler.entity.po.JobEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface JobMapper extends BaseMapper<JobEntity> {

}
