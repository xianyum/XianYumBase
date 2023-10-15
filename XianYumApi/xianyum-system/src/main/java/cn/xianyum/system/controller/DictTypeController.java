package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import cn.xianyum.system.service.DictTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
@RequestMapping("/xianyum-system/v1/dict/type")
@Api(tags = "字典类型接口")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取字典类型列表")
    public Result list(DictTypeRequest request){
        IPage<DictTypeEntity> list = dictTypeService.selectDictTypeList(request);
        return Result.page(list);
    }


    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询字典类型详细")
    public Result getInfo(@PathVariable Long id) {
        return Result.success(dictTypeService.selectDictTypeById(id));
    }

    /**
     * 新增字典类型
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增字典类型")
    @SysLog(value = "新增字典类型")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result save(@RequestBody DictTypeEntity dictTypeEntity) {
        return Result.success(dictTypeService.save(dictTypeEntity));
    }


    /**
     * 更新字典类型详细
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新字典类型详细")
    @SysLog(value = "更新字典类型详细")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result update(@RequestBody DictTypeEntity dictTypeEntity) {
        return Result.success(dictTypeService.update(dictTypeEntity));
    }

    @ApiOperation(value = "刷新字典缓存")
    @DeleteMapping("/refreshCache")
    @SysLog(value = "刷新字典缓存")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result refreshCache() {
        dictTypeService.resetDictCache();
        return Result.success();
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "批量删除字典类型")
    @SysLog(value = "批量删除字典类型")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result remove(@PathVariable Long[] ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return Result.success();
    }


    @GetMapping("/optionSelect")
    @ApiOperation(value = "获取字典选择框列表")
    public Result optionSelect() {
        List<DictTypeEntity> resultList = dictTypeService.optionSelect();
        return Result.success(resultList);
    }
}
