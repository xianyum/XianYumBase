package cn.xianyum.system.service;

import cn.xianyum.common.entity.file.FileDetailResponse;
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
    FileDetailResponse uploadFile(MultipartFile file);


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
