package com.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.common.utils.DataResult;
import com.base.common.utils.UUIDUtils;
import com.base.entity.po.LogEntity;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.XiaoDaoRequest;
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
@RequestMapping("/xiaodao")
@Api(tags = "小刀爬取数据接口")
public class XiaoDaoController {

    @Autowired
    private XiaoDaoService xiaoDaoService;


    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public DataResult list(@RequestBody XiaoDaoRequest request){
        IPage<XiaoDaoEntity> list = xiaoDaoService.queryAll(request);
        return DataResult.success(list);
    }
}
