package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.service.GiteeSerivce;
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
 * @date 2020/11/21 16:12
 */
@RestController
@RequestMapping("/xianyum-system/v1/gitee")
@Api(tags = "Gitee相关接口")
@Slf4j
public class GiteeController {

    @Autowired
    private GiteeSerivce giteeSerivce;

    @PostMapping("/push")
    @ApiOperation(value = "接受仓库推送请求")
    @Permission(publicApi = true)
    public DataResult getIpInfo(@RequestBody String json) {
        log.info("接受的参数:{}",json);
        giteeSerivce.push(json);
        return DataResult.success();
    }
}
