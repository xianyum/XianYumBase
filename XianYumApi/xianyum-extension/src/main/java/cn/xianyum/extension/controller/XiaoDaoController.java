package cn.xianyum.extension.controller;

import cn.xianyum.extension.entity.request.XiaoDaoRequest;
import cn.xianyum.extension.entity.response.XiaoDaoResponse;
import cn.xianyum.extension.service.XiaoDaoService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2019/9/25 17:02
 */
@RestController
@RequestMapping("xym-extension/v1/xiaodao")
@Tag(name = "小刀爬取数据接口")
public class XiaoDaoController {

    @Autowired
    private XiaoDaoService xiaoDaoService;

    @GetMapping("/getPage")
    @Operation(summary = "获取小刀数据列表")
    public Results getPage(XiaoDaoRequest request){
        PageResponse<XiaoDaoResponse> list = xiaoDaoService.getPage(request);
        return Results.page(list);
    }
}
