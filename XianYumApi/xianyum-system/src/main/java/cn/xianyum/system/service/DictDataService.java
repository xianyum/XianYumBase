package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
public interface DictDataService {
    List<DictDataEntity> selectDictDataByType(String dictType);

    IPage<DictDataEntity> selectDictDataList(DictDataRequest request);

    DictDataEntity getInfo(Long id);

    int save(DictDataEntity dict);

    int update(DictDataEntity dict);

    void deleteDictDataByIds(Long[] ids);
}
