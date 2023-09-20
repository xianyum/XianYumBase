package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DataResult;
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
@RequestMapping("/log")
@Api(tags = "系统日志接口")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获取系统日志
     */
    @PostMapping("/getPage")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public DataResult list(@RequestBody LogRequest request){
        IPage<LogEntity> list = logService.queryAll(request);
        return DataResult.success(list);
    }

    /**
     * 获取七天趋势图
     */
    @PostMapping("/getVisitCountCharts")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public DataResult getVisitCountCharts(@RequestBody LogRequest request){
        List<LogResponse> responses = logService.getVisitCountCharts(request);
        return DataResult.success(responses);
    }

    @DeleteMapping("/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "删除日志记录", httpMethod = "POST")
    public DataResult delete(@RequestBody String[] logIdS){
        try {
            logService.deleteById(logIdS);
            return DataResult.success();
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
    }

    @DeleteMapping("/truncateLog")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "清空日志", httpMethod = "POST")
    @SysLog(value = "清空用户操作日志")
    public DataResult truncateLog(){
        try {
            logService.truncateLog();
            return DataResult.success();
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
    }
}
