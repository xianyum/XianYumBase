package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DataResult;
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
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 所有用户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public DataResult list(@RequestBody UserRequest user){
        IPage<UserEntity> list = userService.queryAll(user);
        return DataResult.success(list);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取登录的用户信息", httpMethod = "GET")
    public DataResult info(){
        UserEntity userEntity = userService.getInfo();
        return DataResult.success().put("user", userEntity);
    }

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    @PostMapping("/delete")
    @SysLog(value = "删除用户")
    @Permissions(value = {"visitor","common"})
    @ApiOperation(value = "删除用户", httpMethod = "POST")
    public DataResult delete(@RequestBody String[] userIds){
        try {
            userService.deleteById(userIds);
            return DataResult.success();
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
    }

    /**
     * 根据Id查询用户
     */
    @PostMapping("/selectOneById")
    @ApiOperation(value = "根据Id查询用户", httpMethod = "POST")
    public DataResult selectOneById(@RequestBody UserRequest user){
        UserEntity info = userService.selectOneById(user);
        return DataResult.success(info);
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    @SysLog(value = "新增用户")
    @ApiOperation(value = "保存用户", httpMethod = "POST")
    @Permissions(value = {"visitor","common"})
    public DataResult save(@RequestBody UserRequest user){
        try {
            ValidatorUtils.validateEntity(user);
            int count = userService.save(user);
            if(count>0){
                return DataResult.success();
            }else {
                return DataResult.error("保存用户失败！");
            }
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    @SysLog(value = "修改用户")
    @ApiOperation(value = "修改用户", httpMethod = "POST", notes = "修改用户")
    @Permissions(value = {"visitor","common"})
    public DataResult update(@RequestBody UserRequest user){
        try {
            int count = userService.update(user);
            if(count>0){
                return DataResult.success();
            }else {
                return DataResult.error("修改用户失败！");
            }
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
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
     * 更新当前用户信息
     */
    @PostMapping ("/updateCurrentUser")
    @SysLog(value = "更新当前用户信息")
    @ApiOperation(value = "更新当前用户信息", httpMethod = "POST")
    public DataResult updateCurrentUser(@RequestBody UserRequest user){
        int count = userService.updateCurrentUser(user);
        if(count>0){
            return DataResult.success();
        }else {
            return DataResult.error("更新当前用户信息失败！");
        }
    }


    @PostMapping("/upload")
    @ApiOperation(value = "上传图像接口", httpMethod = "POST")
    public DataResult upload(@RequestParam("file") MultipartFile file){
        String imageUrl = userService.upload(file);
        return DataResult.success(imageUrl);
    }
}