package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.system.infra.server.Server;
import cn.xianyum.common.utils.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2019/5/25 20:51
 * @email 80616059@qq.com
 */
@RestController
@RequestMapping("xym-system/v1/server")
@Api(tags = "服务器信息接口")
public class ServerController {

    @GetMapping("/get")
    @ApiOperation(value = "获取服务器相关信息")
    @Permission("@ps.hasPerm('monitor:server:list')")
    public Results getServerInfo(){
        try {
            Server server = new Server();
            server.copyTo();
            return Results.success(server);
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }catch(Exception exception){
            return Results.error("系统异常");
        }
    }
}
