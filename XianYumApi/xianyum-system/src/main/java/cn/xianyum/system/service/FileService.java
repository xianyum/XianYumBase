package cn.xianyum.system.service;

import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.common.entity.file.PresignedUploadResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;

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

    /**
     * 生成上传预签名URL，供前端直传存储平台
     * @param filename 文件名
     * @return
     */
    PresignedUploadResponse generateUploadPresignedUrl(String filename);

    /**
     * 文件持久化
     * @param fileIds
     */
    void updateUploadStatusToComplete(List<String> fileIds);

    /**
     * 删除文件：删除远程文件及文件记录
     * @param fileIds 文件id集合
     */
    void deleteFiles(List<String> fileIds);
}
