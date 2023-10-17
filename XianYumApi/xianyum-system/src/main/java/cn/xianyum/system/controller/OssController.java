package cn.xianyum.system.controller;


import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.po.OssFileEntity;
import cn.xianyum.system.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangwei
 * @date 2019/10/22 16:09
 */
@RestController
@RequestMapping("/oss")
@Api(tags = "文件oss接口")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传接口", httpMethod = "POST")
    @Permission()
    public Results upload(@RequestParam("file") MultipartFile file)  throws Exception{
        OssFileEntity ossFileEntity = ossService.upload(file);
        if(ossFileEntity == null){
            return Results.error("上传失败！");
        }
        return Results.success(ossFileEntity);
    }


    @PostMapping("/getWebUpToken")
    @ApiOperation(value = "获取上传凭证", httpMethod = "POST")
    @Permission()
    public Results getWebUpToken(){

        String token = ossService.getWebUpToken();
        return Results.success(token);
    }

    @RequestMapping(value = "/getImage", produces = "image/jpg", method = RequestMethod.GET)
    public byte[] getImage(@RequestParam("path")String path) {
        try {
            byte[] fr  = ossService.getImage(path);
            return  fr;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
