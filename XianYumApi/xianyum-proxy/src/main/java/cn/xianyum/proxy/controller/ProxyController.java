package cn.xianyum.proxy.controller;


import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import cn.xianyum.proxy.service.ProxyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * 客户端管理接口
 *
 */
@Api(tags = "客户端管理接口")
@RestController
@RequestMapping(value = "/proxy")
@Slf4j
public class ProxyController {

	@Autowired
	private ProxyService proxyService;

    /**
     * 客户端管理分页查询数据
     *
     */
	@ApiOperation(value = "客户端管理分页查询数据")
	@PostMapping(value = "/getPage")
	public DataResult getPage(@RequestBody ProxyRequest request) {

		IPage<ProxyResponse> response = proxyService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 客户端管理根据ID查询数据
     *
     */
    @ApiOperation(value = "客户端管理根据ID查询数据")
    @PostMapping(value = "/getById")
    public DataResult getById(@RequestBody ProxyRequest request) {

        ProxyResponse response = proxyService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 客户端管理保存数据
	 *
     */
    @ApiOperation(value = "客户端管理保存数据")
    @PostMapping(value = "/save")
    public DataResult save(@RequestBody ProxyRequest request) {

		Integer count = proxyService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 客户端管理修改数据
	 *
     */
    @ApiOperation(value = "客户端管理修改数据")
    @PostMapping(value = "/update")
    public DataResult update(@RequestBody ProxyRequest request) {

		Integer count = proxyService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 客户端管理删除数据
	 *
     */
    @ApiOperation(value = "客户端管理删除数据")
    @PostMapping(value = "/delete")
    public DataResult delete(@RequestBody String[] ids) {

		proxyService.deleteById(ids);
	    return DataResult.success();
    }

    /**
     * 获取客户端所有数据
     *
     */
    @ApiOperation(value = "获取客户端所有数据")
    @PostMapping(value = "/getList")
    public DataResult getList(@RequestBody ProxyRequest request) {

        List<ProxyResponse> response = proxyService.getList(request);
        return DataResult.success(response);
    }


    /**
     * 数据刷入系统
     *
     */
    @ApiOperation(value = "刷入系统")
    @PostMapping(value = "/flushProxy")
    public DataResult flushProxy(@RequestBody ProxyRequest request) {
        proxyService.flushProxy();
        return DataResult.success();
    }


    /**
     * 发送客户端配置信息
     *
     */
    @SysLog("发送客户端配置信息")
    @ApiOperation(value = "发送客户端配置信息")
    @PostMapping(value = "/sendEmail")
    public DataResult sendEmail(@RequestBody ProxyRequest request) {
        String result = proxyService.sendEmail(request);
        return DataResult.success(result);
    }

    /**
     * 下载配置
     * @param request
     * @return
     */
    @ApiOperation(value = "生成客户端配置信息")
    @PostMapping(value = "/downloadConfig")
    public void downloadConfig(@RequestBody ProxyRequest request, HttpServletResponse response) {
        PrintWriter writer = null;
        try {
            String configInfo = proxyService.downloadConfig(request);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            writer = response.getWriter();
            writer.println(configInfo);
        }catch (Exception e){
            log.info("下载远程配置config信息异常.",e);
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}
