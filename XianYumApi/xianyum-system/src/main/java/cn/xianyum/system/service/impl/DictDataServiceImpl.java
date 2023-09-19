package cn.xianyum.system.service.impl;

import cn.xianyum.system.dao.DictDataMapper;
import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import cn.xianyum.system.service.DictDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
@Service
public class DictDataServiceImpl implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Override
    public List<DictDataEntity> selectDictDataByType(String dictType) {
        QueryWrapper<DictDataEntity> queryWrapper = new QueryWrapper<DictDataEntity>();
        queryWrapper.eq("dict_type",dictType).eq("status","0").orderByAsc("dict_sort");
        return dictDataMapper.selectList(queryWrapper);
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
        return dictDataMapper.insert(dict);
    }

    @Override
    public int update(DictDataEntity dict) {
        return dictDataMapper.updateById(dict);
    }

    @Override
    public void deleteDictDataByIds(Long[] ids) {
        for(Long id : ids){
            dictDataMapper.deleteById(id);
        }
    }
}
