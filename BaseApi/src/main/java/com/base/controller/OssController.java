package com.base.controller;

import com.base.common.utils.DataResult;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author zhangwei
 * @date 2019/10/22 16:09
 */
@RestController
@RequestMapping("/oss")
@Api(tags = "文件上传接口")
@Slf4j
public class OssController {

    @PostMapping("/upload")
    public DataResult upload(@RequestParam("file") MultipartFile file)  throws Exception{
        //获取到文件后缀名
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        String accessKey = "BO-n8TYUy-DqdUYOyN8RXLZpJYfqP9ghJLEAxGpa";
        String secretKey = "cTgGZFdV4FaLnjlQEBv2U2sH8D70oH31U6beaidY";
        String bucket = "xianyudream";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String fileName = UUID.randomUUID().toString()+suffix;
        Response response = uploadManager.put(file.getBytes(), fileName, upToken);
        log.error(response.toString());
        return DataResult.success("http://pzrmjh002.bkt.clouddn.com/"+fileName);
    }
}
