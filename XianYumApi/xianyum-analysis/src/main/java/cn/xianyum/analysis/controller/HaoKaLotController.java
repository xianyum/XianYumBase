package cn.xianyum.analysis.controller;

import cn.xianyum.analysis.service.HaoKaLotService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2023/10/5 20:27
 */
@RestController
@RequestMapping("/p1/haoKaLot")
@Api(tags = "172号卡系统")
public class HaoKaLotController {

    @Autowired
    HaoKaLotService haoKaLotService;

    @GetMapping("/test")
    public void test(){
        haoKaLotService.pushArticleMessage(null, null);
    }

}
