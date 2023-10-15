package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.service.UserOnlineService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
@RequestMapping("/xianyum-system/v1/online")
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
    public Result list(UserOnlineRequest request){
        IPage<UserOnlineEntity> responsePage = userOnlineService.queryPage(request);
        return Result.page(responsePage);
    }

    /**
     * 踢出用户
     * @param tokenIds
     * @return
     */
    @DeleteMapping("/delete")
    @SysLog(value = "踢出用户")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "踢出用户")
    public Result delete(@RequestBody String[] tokenIds){
        userOnlineService.delete(tokenIds);
        return Result.success();
    }

}
