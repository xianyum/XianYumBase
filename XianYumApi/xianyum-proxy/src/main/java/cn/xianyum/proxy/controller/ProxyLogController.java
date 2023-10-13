package cn.xianyum.proxy.controller;

import cn.xianyum.common.utils.DataResult;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;
import cn.xianyum.proxy.service.ProxyLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 远程代理日志接口
 *
 */
@Api(tags = "远程代理日志接口")
@RestController
@RequestMapping(value = "/xianyum-proxy/v1/proxyLog")
@Slf4j
public class ProxyLogController {

	@Autowired
	private ProxyLogService proxyLogService;

    /**
     * 远程代理日志分页查询数据
     *
     */
	@ApiOperation(value = "远程代理日志分页查询数据")
	@GetMapping(value = "/getPage")
	public DataResult getPage(ProxyLogRequest request) {

		IPage<ProxyLogResponse> response = proxyLogService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 远程代理日志根据ID查询数据
     *
     */
    @ApiOperation(value = "远程代理日志根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public DataResult getById(@PathVariable Long id) {
        ProxyLogResponse response = proxyLogService.getById(id);
        return DataResult.success(response);
    }

    /**
     * 远程代理日志保存数据
	 *
     */
    @ApiOperation(value = "远程代理日志保存数据")
    @PostMapping(value = "/save")
    public DataResult save(@RequestBody ProxyLogRequest request) {

		Integer count = proxyLogService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 远程代理日志修改数据
	 *
     */
    @ApiOperation(value = "远程代理日志修改数据")
    @PutMapping(value = "/update")
    public DataResult update(@RequestBody ProxyLogRequest request) {
		Integer count = proxyLogService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 远程代理日志删除数据
	 *
     */
    @ApiOperation(value = "远程代理日志删除数据")
    @PostMapping(value = "/delete")
    public DataResult delete(@RequestBody Long[] ids) {
		proxyLogService.deleteById(ids);
	    return DataResult.success();
    }

}
