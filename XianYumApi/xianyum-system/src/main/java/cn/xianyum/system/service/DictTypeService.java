package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import cn.xianyum.system.entity.response.DictTypeResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
public interface DictTypeService {
    PageResponse<DictTypeResponse> selectDictTypeList(DictTypeRequest request);

    DictTypeResponse selectDictTypeById(Long id);

    int save(DictTypeEntity dictTypeEntity);

    int update(DictTypeEntity dictTypeEntity);

    boolean checkDictTypeUnique(DictTypeEntity dict);

    void resetDictCache();

    void deleteDictTypeByIds(Long[] ids);

    List<DictTypeEntity> optionSelect();
}
