package cn.xianyum.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.entity.response.UserOnlineResponse;
import cn.xianyum.system.service.UserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2021/1/28 21:58
 */
@RestController
@RequestMapping("xym-system/v1/online")
@Tag(name = "在线用户接口")
@Slf4j
public class UserOnlineController {

    @Autowired
    private UserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     */
    @GetMapping("/getPage")
    @Operation(summary = "获取在线用户列表")
    @SaCheckPermission("monitor:online:page")
    public Results<UserOnlineResponse> list(UserOnlineRequest request){
        PageResponse<UserOnlineResponse> responsePage = userOnlineService.queryPage(request);
        return Results.page(responsePage);
    }

    /**
     * 踢出用户
     * @param tokenIds
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "踢出用户")
    @SaCheckPermission("monitor:online:exit")
    public Results<?> delete(@RequestBody String[] tokenIds){
        userOnlineService.delete(tokenIds);
        return Results.success();
    }

}
