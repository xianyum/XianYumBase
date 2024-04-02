package cn.xianyum.analysis.controller;

import cn.xianyum.analysis.entity.request.HaoKaLotProductRequest;
import cn.xianyum.analysis.entity.response.HaoKaLotProductResponse;
import cn.xianyum.analysis.service.HaoKaLotService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2023/10/5 20:27
 */
@RestController
@RequestMapping("xianyum-analysis/v1/haoKaLot")
@Api(tags = "172号卡系统")
public class HaoKaLotController {

    @Autowired
    HaoKaLotService haoKaLotService;

    @GetMapping("/getPage")
    @ApiOperation(value = "获取172号卡商品列表")
    public Results getPage(HaoKaLotProductRequest request){
        PageResponse<HaoKaLotProductResponse> list = haoKaLotService.getPage(request);
        return Results.page(list);
    }

}
