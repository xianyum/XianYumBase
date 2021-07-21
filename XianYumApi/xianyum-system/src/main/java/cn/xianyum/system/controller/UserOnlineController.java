package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.service.UserOnlineService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2021/1/28 21:58
 */
@RestController
@RequestMapping("/online")
@Api(tags = "在线用户接口")
@Slf4j
public class UserOnlineController {

    @Autowired
    private UserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取在线用户列表", httpMethod = "POST")
    public DataResult list(@RequestBody UserOnlineRequest request){
        IPage<UserOnlineEntity> responsePage = userOnlineService.queryPage(request);
        return DataResult.success(responsePage);
    }

    /**
     * 踢出用户
     * @param tokenIds
     * @return
     */
    @PostMapping("/delete")
    @SysLog(value = "踢出用户")
    @Permissions(value = {"visitor","common"})
    @ApiOperation(value = "踢出用户", httpMethod = "POST")
    public DataResult delete(@RequestBody String[] tokenIds){
        userOnlineService.delete(tokenIds);
        return DataResult.success();
    }

}
