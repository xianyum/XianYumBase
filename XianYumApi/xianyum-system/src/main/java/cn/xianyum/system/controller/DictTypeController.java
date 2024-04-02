package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import cn.xianyum.system.entity.response.DictTypeResponse;
import cn.xianyum.system.service.DictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:22
 */
@RestController
@RequestMapping("xym-system/v1/dict/type")
@Api(tags = "字典类型接口")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取字典类型列表")
    @Permission(value = "@ps.hasPerm('system:dict:page')",ignoreDataScope = true)
    public Results list(DictTypeRequest request){
        PageResponse<DictTypeResponse> list = dictTypeService.selectDictTypeList(request);
        return Results.page(list);
    }


    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询字典类型详细")
    @Permission(value = "@ps.hasPerm('system:dict:query')",ignoreDataScope = true)
    public Results getInfo(@PathVariable Long id) {
        return Results.success(dictTypeService.selectDictTypeById(id));
    }

    /**
     * 新增字典类型
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增字典类型")
    @Permission("@ps.hasPerm('system:dict:save')")
    public Results save(@RequestBody DictTypeEntity dictTypeEntity) {
        return Results.success(dictTypeService.save(dictTypeEntity));
    }


    /**
     * 更新字典类型详细
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新字典类型详细")
    @Permission("@ps.hasPerm('system:dict:update')")
    public Results update(@RequestBody DictTypeEntity dictTypeEntity) {
        return Results.success(dictTypeService.update(dictTypeEntity));
    }

    @ApiOperation(value = "刷新字典缓存")
    @DeleteMapping("/refreshCache")
    public Results refreshCache() {
        dictTypeService.resetDictCache();
        return Results.success();
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "批量删除字典类型")
    @Permission("@ps.hasPerm('system:dict:delete')")
    public Results remove(@PathVariable Long[] ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return Results.success();
    }


    @GetMapping("/optionSelect")
    @ApiOperation(value = "获取字典选择框列表")
    public Results optionSelect() {
        List<DictTypeEntity> resultList = dictTypeService.optionSelect();
        return Results.success(resultList);
    }
}
