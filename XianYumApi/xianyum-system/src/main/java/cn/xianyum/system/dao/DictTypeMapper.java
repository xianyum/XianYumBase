package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:27
 */
public interface DictTypeMapper extends BaseMapper<DictTypeEntity> {
    List<DictTypeEntity> selectDictTypeList(@Param("request") DictTypeRequest request, Page<DictTypeEntity> page);
}
