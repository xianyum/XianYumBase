package cn.xianyum.system.service;

import org.springframework.web.multipart.MultipartFile;

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
    String uploadFile(MultipartFile file);

    /**
     * 获取预签名url
     * @param fileId
     * @return
     */
    String presignedUrl(String fileId);
}
