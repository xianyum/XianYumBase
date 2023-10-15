package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Result;
import cn.xianyum.common.validator.ValidatorUtils;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/***
 * 用户相关
 */
@RestController
@RequestMapping("/xianyum-system/v1/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 所有用户列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取用户列表")
    public Result list(UserRequest user){
        IPage<UserEntity> list = userService.queryAll(user);
        return Result.page(list);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取登录的用户信息", httpMethod = "GET")
    public Result info(){
        LoginUser userEntity = userService.getUserSelf();
        return Result.success().put("user", userEntity);
    }

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    @DeleteMapping("/delete")
    @SysLog(value = "删除用户")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "删除用户")
    public Result delete(@RequestBody String[] userIds){
        try {
            userService.deleteById(userIds);
            return Result.success();
        }catch(SoException exception){
            return Result.error(exception.getMsg());
        }
    }

    /**
     * 根据Id查询用户
     */
    @GetMapping("/getById/{id}")
    @ApiOperation(value = "根据Id查询用户")
    public Result selectOneById(@PathVariable String id){
        UserEntity info = userService.selectOneById(id);
        return Result.success(info);
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    @SysLog(value = "新增用户")
    @ApiOperation(value = "保存用户", httpMethod = "POST")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result save(@RequestBody UserRequest user){
        try {
            ValidatorUtils.validateEntity(user);
            int count = userService.save(user);
            if(count>0){
                return Result.success();
            }else {
                return Result.error("保存用户失败！");
            }
        }catch(SoException exception){
            return Result.error(exception.getMsg());
        }
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    @SysLog(value = "修改用户")
    @ApiOperation(value = "修改用户", httpMethod = "POST", notes = "修改用户")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result update(@RequestBody UserRequest user){
        try {
            int count = userService.update(user);
            if(count>0){
                return Result.success();
            }else {
                return Result.error("修改用户失败！");
            }
        }catch(SoException exception){
            return Result.error(exception.getMsg());
        }
    }

    @PutMapping("/password")
    @ApiOperation(value = "修改密码", httpMethod = "POST", notes = "修改密码")
    public Result updatePassword(@RequestBody UpdatePasswordRequest info){
        ValidatorUtils.validateEntity(info);
        boolean flag = userService.updatePassword(info);
        if(!flag){
            return Result.error("原密码错误");
        }
        return Result.success();
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping ("/updateCurrentUser")
    @SysLog(value = "更新当前用户信息")
    @ApiOperation(value = "更新当前用户信息", httpMethod = "POST")
    public Result updateCurrentUser(@RequestBody UserRequest user){
        int count = userService.updateCurrentUser(user);
        if(count>0){
            return Result.success();
        }else {
            return Result.error("更新当前用户信息失败！");
        }
    }


    @PostMapping("/upload")
    @ApiOperation(value = "上传图像接口", httpMethod = "POST")
    public Result upload(@RequestParam("file") MultipartFile file){
        String imageUrl = userService.upload(file);
        return Result.success(imageUrl);
    }
}
