package com.base.controller;

import com.base.common.utils.DataResult;
import com.base.common.utils.UUIDUtils;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.service.iservice.XiaoDaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2019/9/25 17:02
 */
@RestController
@RequestMapping("/test")
@Api(tags = "测试接口")
public class TestController {

    @Autowired
    private XiaoDaoService xiaoDaoService;

    /**
     * 获取系统日志
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public DataResult list(@RequestBody String json){
        XiaoDaoEntity xiaoDaoEntity = new XiaoDaoEntity();
        xiaoDaoEntity.setId(UUIDUtils.UUIDReplace());
        return DataResult.success();
    }
}
