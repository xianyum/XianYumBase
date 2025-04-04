package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.validator.ValidatorUtils;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.UserResponse;
import cn.xianyum.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/***
 * 用户相关
 */
@RestController
@RequestMapping("xym-system/v1/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页获取用户列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "分页获取用户列表")
    @Permission(value = "@ps.hasPerm('system:user:page')",ignoreDataScope = true)
    public Results getPage(UserRequest user){
        PageResponse<UserResponse> list = userService.getPage(user);
        return Results.page(list);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取登录的用户信息", httpMethod = "GET")
    public Results info(){
        LoginUser userEntity = userService.getUserSelf();
        return Results.success().put("user", userEntity);
    }

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户")
    @Permission("@ps.hasPerm('system:user:delete')")
    public Results delete(@RequestBody String[] userIds){
        try {
            userService.deleteById(userIds);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }

    /**
     * 根据Id查询用户
     */
    @GetMapping("/getById/{id}")
    @ApiOperation(value = "根据Id查询用户")
    @Permission(value = "@ps.hasPerm('system:user:query')",ignoreDataScope = true)
    public Results selectOneById(@PathVariable String id){
        UserResponse userResponse = userService.selectOneById(id);
        return Results.success(userResponse);
    }


    /**
     * 根据Id查询用户
     */
    @GetMapping("/getByRoleId/{roleId}")
    @ApiOperation(value = "根据角色ID查询用户")
    @Permission(value = "@ps.hasPerm('system:role:user')")
    public Results getByRoleId(@PathVariable String roleId){
        List<UserResponse> userResponse = userService.getByRoleId(roleId);
        return Results.success(userResponse);
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存用户")
    @Permission("@ps.hasPerm('system:user:save')")
    public Results save(@RequestBody UserRequest user){
        try {
            ValidatorUtils.validateEntity(user);
            int count = userService.save(user);
            if(count>0){
                return Results.success();
            }else {
                return Results.error("保存用户失败！");
            }
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    @ApiOperation(value = "修改用户", httpMethod = "POST", notes = "修改用户")
    @Permission("@ps.hasPerm('system:user:update')")
    public Results update(@RequestBody UserRequest user){
        try {
            ValidatorUtils.validateEntity(user);
            int count = userService.update(user);
            if(count>0){
                return Results.success();
            }else {
                return Results.error("修改用户失败！");
            }
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }

    @PutMapping("/password")
    @ApiOperation(value = "修改密码")
    public Results updatePassword(@RequestBody UpdatePasswordRequest info){
        ValidatorUtils.validateEntity(info);
        boolean flag = userService.updatePassword(info);
        if(!flag){
            return Results.error("原密码错误");
        }
        return Results.success();
    }


    @PutMapping("/changeStatus")
    @ApiOperation(value = "修改用户状态")
    @Permission("@ps.hasPerm('system:user:update')")
    public Results updatePassword(@RequestBody UserRequest request){
        int count = userService.changeStatus(request);
        return Results.success(count);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping ("/updateCurrentUser")
    @ApiOperation(value = "更新当前用户信息")
    public Results updateCurrentUser(@RequestBody UserRequest user){
        int count = userService.updateCurrentUser(user);
        if(count>0){
            return Results.success();
        }else {
            return Results.error("更新当前用户信息失败！");
        }
    }


    @PostMapping("/upload")
    @ApiOperation(value = "上传图像接口", httpMethod = "POST")
    public Results upload(@RequestParam("file") MultipartFile file){
        String imageUrl = userService.upload(file);
        return Results.success(imageUrl);
    }

    /**
     * 个人中心
     */
    @GetMapping("/profile")
    public Results userProfile(){
        UserResponse userResponse = userService.getUserProfile();
        return Results.success(userResponse);
    }

}
