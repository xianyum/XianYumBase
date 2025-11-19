package cn.xianyum.sheduler.dao;

import cn.xianyum.sheduler.entity.po.JobLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface JobLogMapper extends BaseMapper<JobLogEntity> {

    void truncateLog();
}
