package cn.xianyum.system.controller;

import cn.xianyum.common.utils.Results;
import cn.xianyum.system.service.EncryptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author zhangwei
 * @date 2022/7/17 22:40
 */
@RestController
@RequestMapping("/p1/encryption")
@Api(tags = "加密相关接口")
@Slf4j
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/aes/encrypt")
    @ApiOperation(value = "aes加密接口", httpMethod = "GET")
    public Results aesEncrypt(@RequestParam("content") String content) {
        Object result = encryptionService.aesEncrypt(content);
        return Results.success(result);
    }


    @GetMapping("/aes/decrypt")
    @ApiOperation(value = "aes解密接口", httpMethod = "GET")
    public Results aesDecrypt(@RequestParam("content") String content) {
        Object result = encryptionService.aesDecrypt(content);
        return Results.success(result);
    }
}
