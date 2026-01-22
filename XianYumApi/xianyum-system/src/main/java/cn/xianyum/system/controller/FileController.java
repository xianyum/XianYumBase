package cn.xianyum.system.controller;

import cn.xianyum.common.utils.Results;
import cn.xianyum.system.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author xianyum
 * @date 2026/1/19 22:22
 */
@RestController
@RequestMapping("xym-system/v1/file")
@Tag(name = "通用文件上传接口")
public class FileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Results uploadSingleFile(@RequestParam("file") MultipartFile file) {
        String fileId = this.fileService.uploadFile(file);
        return Results.success(fileId);
    }

    @Operation(summary = "获取文件访问url")
    @GetMapping(value = "/presignedUrl")
    public Results presignedUrl(@RequestParam String fileId) {
        return Results.success(this.fileService.presignedUrl(fileId));
    }
}
