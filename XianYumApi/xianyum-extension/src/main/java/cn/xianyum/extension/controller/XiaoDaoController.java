package cn.xianyum.extension.controller;

import cn.xianyum.extension.entity.request.XiaoDaoRequest;
import cn.xianyum.extension.entity.response.XiaoDaoResponse;
import cn.xianyum.extension.service.XiaoDaoService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2019/9/25 17:02
 */
@RestController
@RequestMapping("xym-extension/v1/xiaodao")
@Api(tags = "小刀爬取数据接口")
public class XiaoDaoController {

    @Autowired
    private XiaoDaoService xiaoDaoService;

    @GetMapping("/getPage")
    @ApiOperation(value = "获取小刀数据列表")
    public Results getPage(XiaoDaoRequest request){
        PageResponse<XiaoDaoResponse> list = xiaoDaoService.getPage(request);
        return Results.page(list);
    }
}
