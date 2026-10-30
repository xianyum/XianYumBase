package cn.xianyum.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.IgnoreDataScope;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import cn.xianyum.system.entity.response.DictDataResponse;
import cn.xianyum.system.service.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2023/9/19 17:23
 */
@RestController
@RequestMapping("xym-system/v1/dict/data")
@Tag(name = "字典类型接口")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    @Operation(summary = "根据字典类型查询字典数据信息")
    public Results<List<DictDataEntity>> dictType(@PathVariable String dictType) {
        List<DictDataEntity> data = dictDataService.selectDictDataByType(dictType);
        if (Objects.isNull(data)) {
            data = new ArrayList<>();
        }
        return Results.success(data);
    }

    @GetMapping("/getPage")
    @Operation(summary = "分页查询字典数据信息")
    @IgnoreDataScope
    @SaCheckPermission("system:dict:page")
    public Results<DictDataResponse> list(DictDataRequest dictData) {
        PageResponse<DictDataResponse> list = dictDataService.selectDictDataList(dictData);
        return Results.page(list);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "查询单条信息")
    @IgnoreDataScope
    @SaCheckPermission("system:dict:query")
    public Results<DictDataResponse> getInfo(@PathVariable Long id) {
        DictDataResponse dictDataResponse = dictDataService.getInfo(id);
        return Results.success(dictDataResponse);
    }

    /**
     * 新增字典类型
     */
    @PostMapping("/save")
    @Operation(summary = "新增字典数据")
    @SaCheckPermission("system:dict:save")
    public Results<?> add(@RequestBody DictDataEntity dict) {
        int count = dictDataService.save(dict);
        return Results.success(count);
    }

    /**
     * 修改保存字典数据
     */
    @PutMapping("/update")
    @Operation(summary = "修改字典数据")
    @SaCheckPermission("system:dict:update")
    public Results<?> edit(@RequestBody DictDataEntity dict) {
        int count = dictDataService.update(dict);
        return Results.success(count);
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:delete")
    public Results<?> remove(@PathVariable Long[] ids) {
        dictDataService.deleteDictDataByIds(ids);
        return Results.success();
    }
}
