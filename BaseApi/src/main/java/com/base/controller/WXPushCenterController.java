package com.base.controller;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.common.utils.DataResult;
import com.base.entity.po.wx_center.WxCenterEntity;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.base.entity.request.WxCenterRequest;
import com.base.entity.request.XiaoDaoRequest;
import com.base.service.iservice.WxCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2019/9/29 13:11
 */
@RestController
@RequestMapping("/wxCenter")
@Api(tags = "微信推送中心")
@Slf4j
public class WXPushCenterController {

    @Autowired
    private WxCenterService wxCenterService;

    @PostMapping("/get")
    @ApiOperation(value = "回调接口", httpMethod = "POST")
    public DataResult get(@RequestBody String  json){
        wxCenterService.save(json);
        return DataResult.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取微信推送中心数据列表", httpMethod = "POST")
    public DataResult list(@RequestBody WxCenterRequest request){
        IPage<WxCenterEntity> list = wxCenterService.queryAll(request);
        return DataResult.success(list);
    }
}
