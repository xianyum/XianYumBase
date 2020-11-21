package com.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.common.annotation.Permissions;
import com.base.common.exception.SoException;
import com.base.common.utils.DataResult;
import com.base.entity.po.LogEntity;
import com.base.entity.request.LogRequest;
import com.base.entity.response.LogResponse;
import com.base.service.iservice.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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
    @PostMapping("/list")
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
        Collections.reverse(responses);
        return DataResult.success(responses);
    }

    @PostMapping("/delete")
    @Permissions(value = {"visitor","common"})
    @ApiOperation(value = "删除日志记录", httpMethod = "POST")
    public DataResult delete(@RequestBody String[] logIdS){
        try {
            logService.deleteById(logIdS);
            return DataResult.success();
        }catch(SoException exception){
            return DataResult.error(exception.getMsg());
        }
    }
}
