package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:27
 */
public interface DictDataMapper extends BaseMapper<DictDataEntity> {
    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    public int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);

    int countDictDataByType(String dictType);

    List<DictDataEntity> selectDictDataList(@Param("request") DictDataRequest request, Page<DictDataEntity> page);
}
