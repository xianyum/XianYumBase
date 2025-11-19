package cn.xianyum.extension.controller;

import cn.xianyum.extension.entity.request.HaoKaLotProductRequest;
import cn.xianyum.extension.entity.response.HaoKaLotProductResponse;
import cn.xianyum.extension.service.HaoKaLotService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2023/10/5 20:27
 */
@RestController
@RequestMapping("xym-extension/v1/haoKaLot")
@Tag(name = "172号卡系统")
public class HaoKaLotController {

    @Autowired
    HaoKaLotService haoKaLotService;

    @GetMapping("/getPage")
    @Operation(summary = "获取172号卡商品列表")
    public Results getPage(HaoKaLotProductRequest request){
        PageResponse<HaoKaLotProductResponse> list = haoKaLotService.getPage(request);
        return Results.page(list);
    }

}
