package cn.xianyum.system.service;

import cn.xianyum.common.entity.file.FileDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author xianyum
 * @date 2026/1/19 22:50
 */
public interface FileService {

    /**
     * 上传文件，并返回文件ID
     * @param file
     * @return
     */
    FileDetailResponse uploadFile(MultipartFile file);

    /**
     * 上传文件，指定存储路径
     * @param file
     * @param path 自定义存储路径，如 "custom/dir/"
     * @return
     */
    FileDetailResponse uploadFile(MultipartFile file, String path);

    /**
     * 上传本地文件，使用默认日期路径
     * @param file 本地File对象
     * @return
     */
    FileDetailResponse uploadFile(File file);

    /**
     * 上传本地文件，指定存储路径
     * @param file 本地File对象
     * @param path 自定义存储路径，如 "custom/dir/"
     * @return
     */
    FileDetailResponse uploadFile(File file, String path);

    /**
     * 根据文件id获取响应数据
     * @param id
     * @param isCached
     * @return
     */
    FileDetailResponse selectFileById(String id,boolean isCached);

    /**
     * 获取预签名url
     * @param fileId
     * @return
     */
    String presignedUrl(String fileId);
}
