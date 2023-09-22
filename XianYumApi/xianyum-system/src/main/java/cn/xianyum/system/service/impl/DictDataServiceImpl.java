package cn.xianyum.system.service.impl;

import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.system.dao.DictDataMapper;
import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import cn.xianyum.system.infra.adaptor.DictCacheAdaptor;
import cn.xianyum.system.service.DictDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
@Service
public class DictDataServiceImpl implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictCacheAdaptor dictCacheAdaptor;

    @Override
    public List<DictDataEntity> selectDictDataByType(String dictType) {
        if(dictCacheAdaptor.hasDictDataByTypeKey(dictType)){
            return dictCacheAdaptor.getDictDataByTypeCache(dictType);
        }
        QueryWrapper<DictDataEntity> queryWrapper = new QueryWrapper<DictDataEntity>();
        queryWrapper.eq("dict_type",dictType).eq("status","0").orderByAsc("dict_sort");
        List<DictDataEntity> dictDataEntities = dictDataMapper.selectList(queryWrapper);
        if(Objects.nonNull(dictDataEntities) && dictDataEntities.size() > 0){
            dictCacheAdaptor.setDictDataByTypeCache(dictType,dictDataEntities);
        }
        return dictDataEntities;
    }

    @Override
    public IPage<DictDataEntity> selectDictDataList(DictDataRequest request) {
        Page<DictDataEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        List<DictDataEntity> dictTypeEntityList = dictDataMapper.selectDictDataList(request,page);
        page.setRecords(dictTypeEntityList);
        return page;
    }

    @Override
    public DictDataEntity getInfo(Long id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    public int save(DictDataEntity dict) {
        dict.setCreateTime(new Date());
        dict.setCreateBy(SecurityUtils.getLoginUser().getUsername());
        return dictDataMapper.insert(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(DictDataEntity dict) {
        dict.setUpdateTime(new Date());
        dict.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        dictCacheAdaptor.delDictDataByTypeKey(dict.getDictType());
        return dictDataMapper.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataByIds(Long[] ids) {
        for(Long id : ids){
            DictDataEntity dictDataEntity = dictDataMapper.selectById(id);
            if(Objects.nonNull(dictDataEntity)){
                dictCacheAdaptor.delDictDataByTypeKey(dictDataEntity.getDictType());
            }
            dictDataMapper.deleteById(id);
        }
    }
}
