package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import cn.xianyum.system.entity.response.DictDataResponse;
import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
public interface DictDataService {
    List<DictDataEntity> selectDictDataByType(String dictType);

    PageResponse<DictDataResponse> selectDictDataList(DictDataRequest request);

    DictDataResponse getInfo(Long id);

    int save(DictDataEntity dict);

    int update(DictDataEntity dict);

    void deleteDictDataByIds(Long[] ids);
}
