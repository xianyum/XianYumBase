package cn.xianyum.extension.dao;

import cn.xianyum.extension.entity.po.EvTripPointEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EvTripPointMapper extends BaseMapper<EvTripPointEntity> {

    int savBatch(@Param("list") List<EvTripPointEntity> list);
}
