package cn.xianyum.analysis.controller;

import cn.xianyum.analysis.entity.po.HaoKaLotProductEntity;
import cn.xianyum.analysis.entity.request.HaoKaLotProductRequest;
import cn.xianyum.analysis.service.HaoKaLotService;
import cn.xianyum.common.utils.DataResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
@RequestMapping("/xianyum-analysis/v1/haoKaLot")
@Api(tags = "172号卡系统")
public class HaoKaLotController {

    @Autowired
    HaoKaLotService haoKaLotService;

    @GetMapping("/getPage")
    @ApiOperation(value = "获取172号卡商品列表")
    public DataResult getPage(HaoKaLotProductRequest request){
        IPage<HaoKaLotProductEntity> list = haoKaLotService.getPage(request);
        return DataResult.success(list);
    }

}
