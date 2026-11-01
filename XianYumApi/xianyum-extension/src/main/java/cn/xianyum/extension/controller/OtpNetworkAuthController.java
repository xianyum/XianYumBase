package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.entity.request.OtpNetworkAuthRequest;
import cn.xianyum.extension.entity.response.OtpNetworkAuthResponse;
import cn.xianyum.extension.service.OtpNetworkAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * OTP网络验证Controller
 * @author xianyum
 * @date 2026/3/25 21:12
 */
@Tag(name = "OTP网络验证")
@RestController
@RequestMapping("/xym-extension/v1/otp-network-auth")
@OperLog(module = "OTP网络验证")
public class OtpNetworkAuthController {

    @Resource
    private OtpNetworkAuthService otpNetworkAuthService;

    /**
     * 保存OTP网络验证
     * @param request 请求参数
     * @return 保存结果
     */
    @Operation(summary = "保存OTP网络验证")
    @PostMapping("/save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增OTP网络验证")
    public Results<Boolean> save(@RequestBody OtpNetworkAuthRequest request) {
        boolean result = otpNetworkAuthService.save(request);
        return Results.success(result);
    }

    /**
     * 更新系统名称
     * @param request 请求参数
     * @return 保存结果
     */
    @Operation(summary = "更新系统名称")
    @PutMapping("/update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改OTP网络验证")
    public Results<Boolean> update(@RequestBody OtpNetworkAuthRequest request) {
        boolean result = otpNetworkAuthService.update(request);
        return Results.success(result);
    }

    /**
     * 获取OTP网络验证列表
     * @return OTP网络验证列表
     */
    @Operation(summary = "获取OTP网络验证列表")
    @GetMapping("/getList")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "查询OTP网络验证列表")
    public Results<List<OtpNetworkAuthResponse>> getList() {
        List<OtpNetworkAuthResponse> list = otpNetworkAuthService.getList();
        return Results.success(list);
    }

    @Operation(summary = "删除OTP网络验证")
    @DeleteMapping(value = "/delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除OTP网络验证")
    public Results<?> delete(@RequestParam String id) {
        return Results.success(otpNetworkAuthService.deleteById(id));
    }
}
