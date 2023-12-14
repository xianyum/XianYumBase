package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.entity.response.UserOnlineResponse;
import cn.xianyum.system.service.UserOnlineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2021/1/28 21:58
 */
@RestController
@RequestMapping("xianyum-system/v1/online")
@Api(tags = "在线用户接口")
@Slf4j
public class UserOnlineController {

    @Autowired
    private UserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取在线用户列表")
    @Permission("@ps.hasPerm('monitor:online:page')")
    public Results list(UserOnlineRequest request){
        PageResponse<UserOnlineResponse> responsePage = userOnlineService.queryPage(request);
        return Results.page(responsePage);
    }

    /**
     * 踢出用户
     * @param tokenIds
     * @return
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "踢出用户")
    @Permission("@ps.hasPerm('monitor:online:exit')")
    public Results delete(@RequestBody String[] tokenIds){
        userOnlineService.delete(tokenIds);
        return Results.success();
    }

}
