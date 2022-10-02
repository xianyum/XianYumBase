package cn.xianyum.system.controller;

import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.service.FeiShuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2022/4/18 21:36
 */
@RestController
@RequestMapping("/p1/feishu")
@Api(tags = "Gitee相关接口")
@Slf4j
public class FeiShuController {

    @Autowired
    private FeiShuService feiShuService;

    @GetMapping("/callback")
    @ApiOperation(value = "飞书回调接口", httpMethod = "GET")
    public DataResult callback(@RequestParam("code") String code,@RequestParam String key) {
        String result = feiShuService.callback(code,key);
        return DataResult.success(result);
    }
}
