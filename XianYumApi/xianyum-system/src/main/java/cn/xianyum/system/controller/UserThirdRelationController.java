package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.response.UserThirdRelationResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.system.service.UserThirdRelationService;

import java.util.List;

/**
 * (UserThirdRelation)Controller
 *
 * @author makejava
 * @since 2024-03-05 15:03:57
 */
@RestController
@RequestMapping("xianyum-system/v1/userThirdRelation")
@Api(tags = "三方平台账号接口")
public class UserThirdRelationController{

    @Autowired
    private UserThirdRelationService userThirdRelationService;


    @GetMapping("/getCurrentUserThirdRelation")
    @ApiOperation(value = "获取当前用户绑定三方账号列表")
    @Permission(ignoreDataScope = true)
    public Results getCurrentUserThirdRelation() {
        List<UserThirdRelationResponse> responseList = userThirdRelationService.getCurrentUserThirdRelation();
        return Results.success(responseList);
    }

    /**
     * 解除绑定三方账号
     *
     * @param id 主键
     * @return 解除绑定
     */
	@ApiOperation(value = "解除绑定三方账号")
    @DeleteMapping(value = "/unbind")
    public Results unbind(@RequestBody String id) {
        return Results.success(this.userThirdRelationService.unbind(id));
    }
}
