package com.base.controller;


import com.base.common.server.Server;
import com.base.common.utils.DataResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zhangwei
 * @date 2019/4/1 14:54
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/get")
    @ApiOperation(value = "获取服务器相关信息", httpMethod = "GET", notes = "获取服务器相关信息")
    public DataResult getServerInfo() throws Exception{
        Server server = new Server();
        server.copyTo();
        return DataResult.success(server);
    }
}
