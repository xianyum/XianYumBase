package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Result;
import cn.xianyum.system.entity.po.LogEntity;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import cn.xianyum.system.service.LogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author zhangwei
 * @date 2019/6/20 14:16
 */
@RestController
@RequestMapping("/xianyum-system/v1/log")
@Api(tags = "系统日志接口")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获取系统日志
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public Result list(LogRequest request){
        IPage<LogEntity> list = logService.queryAll(request);
        return Result.page(list);
    }

    /**
     * 获取七天趋势图
     */
    @GetMapping("/getVisitCountCharts")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public Result getVisitCountCharts(){
        List<LogResponse> responses = logService.getVisitCountCharts();
        return Result.success(responses);
    }

    @DeleteMapping("/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "删除日志记录", httpMethod = "POST")
    public Result delete(@RequestBody String[] logIdS){
        try {
            logService.deleteById(logIdS);
            return Result.success();
        }catch(SoException exception){
            return Result.error(exception.getMsg());
        }
    }

    @DeleteMapping("/truncateLog")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "清空日志", httpMethod = "POST")
    @SysLog(value = "清空用户操作日志")
    public Result truncateLog(){
        try {
            logService.truncateLog();
            return Result.success();
        }catch(SoException exception){
            return Result.error(exception.getMsg());
        }
    }


    /**
     * 获取接口日志数量
     *
     */
    @ApiOperation(value = "获取接口日志数量")
    @GetMapping(value = "/getLogCount")
    public Result getLogCount() {
        return Result.success(logService.getLogCount());
    }
}
