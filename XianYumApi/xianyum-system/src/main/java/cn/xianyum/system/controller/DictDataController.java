package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
import cn.xianyum.system.entity.po.DictDataEntity;
import cn.xianyum.system.entity.request.DictDataRequest;
import cn.xianyum.system.service.DictDataService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/xianyum-system/v1/dict/data")
@Api(tags = "字典类型接口")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    public Result dictType(@PathVariable String dictType) {
        List<DictDataEntity> data = dictDataService.selectDictDataByType(dictType);
        if (Objects.isNull(data)) {
            data = new ArrayList<>();
        }
        return Result.success(data);
    }

    @GetMapping("/getPage")
    @ApiOperation(value = "分页查询字典数据信息")
    public Result list(DictDataRequest dictData) {
        IPage<DictDataEntity> list = dictDataService.selectDictDataList(dictData);
        return Result.page(list);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询单条信息")
    public Result getInfo(@PathVariable Long id) {
        DictDataEntity dictDataEntity = dictDataService.getInfo(id);
        return Result.success(dictDataEntity);
    }

    /**
     * 新增字典类型
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增字典数据")
    @SysLog(value = "新增字典数据")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result add(@RequestBody DictDataEntity dict) {
        int count = dictDataService.save(dict);
        return Result.success(count);
    }

    /**
     * 修改保存字典数据
     */
    @PutMapping("/update")
    @ApiOperation(value = "修改字典数据")
    @SysLog(value = "修改字典数据")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result edit(@RequestBody DictDataEntity dict) {
        int count = dictDataService.update(dict);
        return Result.success(count);
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除字典数据")
    @SysLog(value = "删除字典数据")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result remove(@PathVariable Long[] ids) {
        dictDataService.deleteDictDataByIds(ids);
        return Result.success();
    }
}
