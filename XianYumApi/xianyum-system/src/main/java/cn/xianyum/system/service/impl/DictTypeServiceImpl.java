package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.dao.DictDataMapper;
import cn.xianyum.system.dao.DictTypeMapper;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import cn.xianyum.system.entity.response.DictTypeResponse;
import cn.xianyum.system.infra.adaptor.DictCacheAdaptor;
import cn.xianyum.system.service.DictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2023/9/19 17:28
 */
@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictCacheAdaptor dictCacheAdaptor;

    @Override
    public PageResponse<DictTypeResponse> selectDictTypeList(DictTypeRequest request) {
        Page<DictTypeEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        List<DictTypeEntity> dictTypeEntityList = dictTypeMapper.selectDictTypeList(request,page);
        page.setRecords(dictTypeEntityList);
        return PageResponse.of(page,DictTypeResponse.class);
    }

    @Override
    public DictTypeResponse selectDictTypeById(Long id) {
        DictTypeEntity dictTypeEntity = dictTypeMapper.selectById(id);
        DictTypeResponse dictTypeResponse = BeanUtils.copy(dictTypeEntity, DictTypeResponse.class);
        return Objects.isNull(dictTypeResponse)?new DictTypeResponse():dictTypeResponse;
    }

    @Override
    public int save(DictTypeEntity dictTypeEntity) {
        boolean isUnique = this.checkDictTypeUnique(dictTypeEntity);
        if(!isUnique){
            throw new SoException("字典类型重复！");
        }
        return dictTypeMapper.insert(dictTypeEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(DictTypeEntity dictTypeEntity) {
        boolean isUnique = this.checkDictTypeUnique(dictTypeEntity);
        if(!isUnique){
            throw new SoException("字典类型重复！");
        }
        DictTypeEntity oldDict = dictTypeMapper.selectById(dictTypeEntity.getId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictTypeEntity.getDictType());
        dictCacheAdaptor.delDictDataByTypeKey(dictTypeEntity.getDictType());
        return dictTypeMapper.updateById(dictTypeEntity);
    }

    @Override
    public boolean checkDictTypeUnique(DictTypeEntity dict) {
        if(StringUtil.isEmpty(dict.getDictType())){
            throw new SoException("字典类型为空！");
        }
        Long dictId = Objects.isNull(dict.getId()) ? -1L : dict.getId();
        QueryWrapper<DictTypeEntity> queryWrapper = new QueryWrapper<DictTypeEntity>()
                .eq("dict_type",dict.getDictType());
        DictTypeEntity dictType = dictTypeMapper.selectOne(queryWrapper);
        if (Objects.nonNull(dictType) && dictType.getId().longValue() != dictId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public void resetDictCache() {
        //TODO
        dictCacheAdaptor.truncateDictDataByTypeKey();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(Long[] ids) {
        for(Long id : ids){
            DictTypeEntity dictTypeEntity = dictTypeMapper.selectById(id);
            int count = dictDataMapper.countDictDataByType(dictTypeEntity.getDictType());
            if(count > 0){
                throw new SoException(String.format("%1$s已分配,不能删除", dictTypeEntity.getDictName()));
            }
            dictCacheAdaptor.delDictDataByTypeKey(dictTypeEntity.getDictType());
            dictTypeMapper.deleteById(id);
        }
    }

    @Override
    public List<DictTypeEntity> optionSelect() {
        QueryWrapper<DictTypeEntity> queryWrapper = new QueryWrapper<>();
        return dictTypeMapper.selectList(queryWrapper);
    }
}
