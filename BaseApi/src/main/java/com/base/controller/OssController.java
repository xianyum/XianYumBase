package com.base.controller;

import com.base.common.utils.DataResult;
import com.base.entity.po.OssFileEntity;
import com.base.service.iservice.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public DataResult upload(@RequestParam("file") MultipartFile file)  throws Exception{
        OssFileEntity ossFileEntity = ossService.upload(file);
        if(ossFileEntity == null){
            return DataResult.error("上传失败！");
        }
        return DataResult.success(ossFileEntity);
    }
}
