package cn.xianyum.zeekr.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.zeekr.entity.po.ZeekrPersonResult;
import cn.xianyum.zeekr.entity.request.ZeekrPersonRequest;
import cn.xianyum.zeekr.entity.response.ZeekrPersonResponse;
import cn.xianyum.zeekr.service.ZeekrPersonInterfaceService;
import cn.xianyum.zeekr.service.ZeekrPersonService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 接口
 *
 */
@Api(tags = "Zeekr Person")
@RestController
@RequestMapping(value = "/v1/zeekrPerson")
@Slf4j
public class ZeekrPersonController {

	@Autowired
	private ZeekrPersonService zeekrPersonService;

    @Autowired
    private ZeekrPersonInterfaceService zeekrPersonInterfaceService;

    /**
     * 分页查询数据
     *
     */
	@ApiOperation(value = "分页查询数据")
	@PostMapping(value = "/getPage")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(@RequestBody ZeekrPersonRequest request) {

		IPage<ZeekrPersonResponse> response = zeekrPersonService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 根据ID查询数据
     *
     */
    @ApiOperation(value = "根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getById(@RequestBody ZeekrPersonRequest request) {

        ZeekrPersonResponse response = zeekrPersonService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 保存数据
	 *
     */
    @ApiOperation(value = "保存数据")
    @PostMapping(value = "/save")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody ZeekrPersonRequest request) {

		Integer count = zeekrPersonService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 修改数据
	 *
     */
    @ApiOperation(value = "修改数据")
    @PostMapping(value = "/update")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody ZeekrPersonRequest request) {

		Integer count = zeekrPersonService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 删除数据
	 *
     */
    @ApiOperation(value = "删除数据")
    @PostMapping(value = "/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult delete(@RequestBody Long[] ids) {

		zeekrPersonService.deleteById(ids);
	    return DataResult.success();
    }


    @ApiOperation(value = "查询项目列表")
    @PostMapping(value = "/getProjectList")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getProjectList(@RequestBody ZeekrPersonRequest request) {

        JSONObject resultObject = zeekrPersonInterfaceService.getZeekrProjectList(request.getLoginName());
        return DataResult.success(resultObject);
    }

    @ApiOperation(value = "获取当月数据")
    @PostMapping(value = "/getZeekrByMonth")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getZeekrByMonth(@RequestBody ZeekrPersonRequest request) {

        List<ZeekrPersonResult> resultObject = zeekrPersonInterfaceService.getZeekrByMonth(request.getLoginName());
        return DataResult.success(resultObject);
    }
}
