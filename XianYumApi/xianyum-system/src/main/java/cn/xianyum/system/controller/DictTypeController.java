package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.DictTypeEntity;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.DictTypeRequest;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.service.DictDataService;
import cn.xianyum.system.service.DictTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 17:22
 */
@RestController
@RequestMapping("/dict/type")
@Api(tags = "字典类型接口")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取字典类型列表", httpMethod = "POST")
    public DataResult list(@RequestBody DictTypeRequest request){
        IPage<DictTypeEntity> list = dictTypeService.selectDictTypeList(request);
        return DataResult.success(list);
    }


    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询字典类型详细", httpMethod = "GET")
    public DataResult getInfo(@PathVariable Long id) {
        return DataResult.success(dictTypeService.selectDictTypeById(id));
    }

    /**
     * 新增字典类型
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增字典类型", httpMethod = "POST")
    @SysLog(value = "新增字典类型")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody DictTypeEntity dictTypeEntity) {
        return DataResult.success(dictTypeService.save(dictTypeEntity));
    }


    /**
     * 更新字典类型详细
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新字典类型详细")
    @SysLog(value = "更新字典类型详细")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody DictTypeEntity dictTypeEntity) {
        return DataResult.success(dictTypeService.update(dictTypeEntity));
    }

    @ApiOperation(value = "刷新字典缓存")
    @DeleteMapping("/refreshCache")
    @SysLog(value = "刷新字典缓存")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult refreshCache() {
        dictTypeService.resetDictCache();
        return DataResult.success();
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "批量删除字典类型")
    @SysLog(value = "批量删除字典类型")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult remove(@PathVariable Long[] ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return DataResult.success();
    }


    @GetMapping("/optionSelect")
    @ApiOperation(value = "获取字典选择框列表", httpMethod = "GET")
    public DataResult optionSelect() {
        List<DictTypeEntity> resultList = dictTypeService.optionSelect();
        return DataResult.success(resultList);
    }
}
