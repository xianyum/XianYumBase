package cn.xianyum.analysis.controller;

import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import cn.xianyum.analysis.service.XiaoDaoService;
import cn.xianyum.common.utils.DataResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    @ApiOperation(value = "获取小刀数据列表", httpMethod = "POST")
    public DataResult list(@RequestBody XiaoDaoRequest request){
        IPage<XiaoDaoEntity> list = xiaoDaoService.queryAll(request);
        return DataResult.success(list);
    }
}
