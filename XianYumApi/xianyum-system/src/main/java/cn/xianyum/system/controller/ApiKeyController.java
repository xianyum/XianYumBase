package cn.xianyum.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.IgnoreDataScope;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.ApiKeyRequest;
import cn.xianyum.system.entity.response.ApiKeyResponse;
import cn.xianyum.system.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Key 接口
 */
@RestController
@RequestMapping("xym-system/v1/apiKey")
@Tag(name = "API Key 接口")
@OperLog(module = "API Key管理")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @GetMapping("/getPage")
    @Operation(summary = "分页查询 API Key")
    @SaCheckPermission("system:apiKey:page")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询 API Key")
    public Results<ApiKeyResponse> getPage(ApiKeyRequest request) {
        PageResponse<ApiKeyResponse> response = apiKeyService.getPage(request);
        return Results.page(response);
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID查询 API Key")
    @SaCheckPermission("system:apiKey:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询 API Key")
    public Results<ApiKeyResponse> getById(@PathVariable Long id) {
        return Results.success(apiKeyService.getById(id));
    }

    @PostMapping("/save")
    @Operation(summary = "新增 API Key")
    @SaCheckPermission("system:apiKey:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增 API Key")
    public Results<?> save(@RequestBody ApiKeyRequest request) {
        int count = apiKeyService.save(request);
        return count > 0 ? Results.success(count) : Results.error("保存失败");
    }

    @PutMapping("/update")
    @Operation(summary = "修改 API Key")
    @SaCheckPermission("system:apiKey:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改 API Key")
    public Results<?> update(@RequestBody ApiKeyRequest request) {
        int count = apiKeyService.update(request);
        return count > 0 ? Results.success(count) : Results.error("修改失败");
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除 API Key")
    @SaCheckPermission("system:apiKey:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除 API Key")
    public Results<?> remove(@PathVariable Long[] ids) {
        apiKeyService.deleteByIds(ids);
        return Results.success();
    }
}
