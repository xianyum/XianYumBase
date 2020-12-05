package com.base.controller;

import com.base.common.utils.DataResult;
import com.base.service.iservice.GiteeSerivce;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2020/11/21 16:12
 */
@RestController
@RequestMapping("/gitee")
@Api(tags = "Gitee相关接口")
@Slf4j
public class GiteeController {

    @Autowired
    private GiteeSerivce giteeSerivce;

    @PostMapping("/push")
    @ApiOperation(value = "接受仓库推送请求", httpMethod = "POST")
    public DataResult getIpInfo(@RequestBody String json) {
        log.info("接受的参数:{}",json);
        giteeSerivce.push(json);
        return DataResult.success();
    }
}
