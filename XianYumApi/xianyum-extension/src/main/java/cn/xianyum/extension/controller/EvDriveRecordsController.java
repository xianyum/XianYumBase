package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.entity.base.PageResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import cn.xianyum.extension.service.EvDriveRecordsService;

/**
 * 新能源车行驶记录(EvDriveRecords)Controller
 *
 * @author makejava
 * @since 2025-03-06 20:43:40
 */
@RestController
@RequestMapping("xym-extension/v1/evDriveRecords")
@Api(tags = "新能源车行驶记录接口")
public class EvDriveRecordsController {

    @Autowired
    private EvDriveRecordsService evDriveRecordsService;

    /**
     * 分页查询新能源车行驶记录
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @ApiOperation(value = "分页查询新能源车行驶记录")
    @GetMapping(value = "/getPage")
    public Results getPage(EvDriveRecordsRequest request) {
        PageResponse<EvDriveRecordsResponse> responsePage = evDriveRecordsService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 根据ID查询新能源车行驶记录
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "根据ID查询新能源车行驶记录")
    @GetMapping("getById/{id}")
    public Results selectOne(@PathVariable Long id) {
        return Results.success(evDriveRecordsService.getById(id));
    }

    /**
     * 新能源车行驶记录保存数据
     *
     * @param request
     * @return 新增结果
     */
    @ApiOperation(value = "新能源车行驶记录保存数据")
    @PostMapping(value = "/save")
    @Permission(value = "@ps.hasPerm('ev-drive-records:save')")
    public Results insert(@RequestBody EvDriveRecordsRequest request) {
        return Results.success(this.evDriveRecordsService.save(request));
    }

    /**
     * 新能源车行驶记录更新数据
     *
     * @param request
     * @return 修改结果
     */
    @ApiOperation(value = "新能源车行驶记录更新数据")
    @PutMapping(value = "/update")
    @Permission(value = "@ps.hasPerm('ev-drive-records:update')")
    public Results update(@RequestBody EvDriveRecordsRequest request) {
        return Results.success(this.evDriveRecordsService.update(request));
    }

    /**
     * 新能源车行驶记录批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
    @ApiOperation(value = "新能源车行驶记录批量删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(value = "@ps.hasPerm('ev-drive-records:delete')")
    public Results delete(@RequestBody Long[] ids) {
        return Results.success(this.evDriveRecordsService.deleteById(ids));
    }
}
