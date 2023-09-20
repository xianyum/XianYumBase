package cn.xianyum.proxy.controller;

import cn.xianyum.common.utils.DataResult;
import cn.xianyum.common.validator.ValidatorUtils;
import cn.xianyum.proxy.entity.request.ProxyDetailsRequest;
import cn.xianyum.proxy.entity.response.ProxyDetailsResponse;
import cn.xianyum.proxy.service.ProxyDetailsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客户端配置详细接口
 *
 */
@Api(tags = "客户端配置详细接口")
@RestController
@RequestMapping(value = "/proxyDetails")
@Slf4j
public class ProxyDetailsController {

	@Autowired
	private ProxyDetailsService proxyDetailsService;

    /**
     * 客户端配置详细分页查询数据
     *
     */
	@ApiOperation(value = "客户端配置详细分页查询数据")
	@PostMapping(value = "/getPage")
	public DataResult getPage(@RequestBody ProxyDetailsRequest request) {
		IPage<ProxyDetailsResponse> response = proxyDetailsService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 客户端配置详细根据ID查询数据
     *
     */
    @ApiOperation(value = "客户端配置详细根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public DataResult getById(@PathVariable String id) {
        ProxyDetailsResponse response = proxyDetailsService.getById(id);
        return DataResult.success(response);
    }

    /**
     * 客户端配置详细保存数据
	 *
     */
    @ApiOperation(value = "客户端配置详细保存数据")
    @PostMapping(value = "/save")
    public DataResult save(@RequestBody ProxyDetailsRequest request) {
        ValidatorUtils.validateEntity(request);
		Integer count = proxyDetailsService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 客户端配置详细修改数据
	 *
     */
    @ApiOperation(value = "客户端配置详细修改数据")
    @PutMapping(value = "/update")
    public DataResult update(@RequestBody ProxyDetailsRequest request) {
        ValidatorUtils.validateEntity(request);
		Integer count = proxyDetailsService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 客户端配置详细删除数据
	 *
     */
    @ApiOperation(value = "客户端配置详细删除数据")
    @DeleteMapping(value = "/delete")
    public DataResult delete(@RequestBody String[] ids) {

		proxyDetailsService.deleteById(ids);
	    return DataResult.success();
    }

}
