package com.base.controller;

import com.base.common.utils.DataResult;
import com.base.common.validator.ValidatorUtils;
import com.base.entity.po.UserEntity;
import com.base.entity.request.UpdatePasswordRequest;
import com.base.entity.request.UserInfoRequest;
import com.base.entity.request.UserRequest;
import com.base.service.iservice.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/***
 * 用户相关
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 所有用户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST", notes = "获取用户列表")
    public DataResult list(@RequestBody UserRequest user){
        PageHelper.startPage(user.pageNum, user.pageSize);
        List<UserEntity> list = userService.queryAll(user.getUsername());
        PageInfo<UserEntity> pageInfo = new PageInfo<>(list);
        return DataResult.success(pageInfo);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取登录的用户信息", httpMethod = "GET", notes = "获取登录的用户信息")
    public DataResult info(){
        UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
        return DataResult.success().put("user", userEntity);
    }

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户", httpMethod = "POST", notes = "删除用户")
    public DataResult delete(@RequestBody Long[] userIds){
        userService.deleteById(userIds);
        return DataResult.success();
    }

    /**
     * 根据Id查询用户
     */
    @PostMapping("/selectOneById")
    @ApiOperation(value = "根据Id查询用户", httpMethod = "POST", notes = "根据Id查询用户")
    public DataResult selectOneById(@RequestBody UserRequest user){
        UserEntity info = userService.selectOneById(user.getId());
        return DataResult.success(info);
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    @RequiresPermissions("admin")
    @ApiOperation(value = "保存用户", httpMethod = "POST", notes = "保存用户")
    public DataResult save(@RequestBody UserEntity user){
        ValidatorUtils.validateEntity(user);
        userService.save(user);
        return DataResult.success();
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改用户", httpMethod = "POST", notes = "修改用户")
    public DataResult update(@RequestBody UserEntity user){
        //ValidatorUtils.validateEntity(user);
        userService.update(user);
        return DataResult.success();
    }

    @PostMapping("/password")
    @ApiOperation(value = "修改密码", httpMethod = "POST", notes = "修改密码")
    public DataResult updatePassword(@RequestBody UpdatePasswordRequest info){
        ValidatorUtils.validateEntity(info);
        boolean flag = userService.updatePassword(info);
        if(!flag){
            return DataResult.error("原密码错误");
        }
        return DataResult.success();
    }

    /**
     * 个人信息修改
     */
    @PostMapping("/updateInfo")
    @ApiOperation(value = "个人信息修改", httpMethod = "POST", notes = "个人信息修改")
    public DataResult updateInfo(@RequestBody UserEntity user){
        userService.updateInfo(user);
        return DataResult.success();
    }

    /**
     * 保存个人信息用户
     */
    @PostMapping("/saveInfo")
    @ApiOperation(value = "管理员添加用户", httpMethod = "POST", notes = "管理员添加用户")
    public DataResult saveInfo(@RequestBody UserInfoRequest request){
        userService.saveInfo(request);
        return DataResult.success();
    }

    /**
     * 查询不同身份用户
     */
    @PostMapping("/getByStatus")
    @ApiOperation(value = "查询不同身份用户", httpMethod = "POST", notes = "查询不同身份用户")
    public DataResult getByStatus(@RequestBody UserEntity user){
        List<UserEntity> response = userService.getByStatus(user);
        return DataResult.success(response);
    }
}
