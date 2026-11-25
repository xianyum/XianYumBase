package cn.xianyum.common.entity.file;

import lombok.Data;

/**
 * 上传预签名URL响应，前端拿到后直接向存储平台上传文件
 *
 * @author xianyum
 * @date 2026/1/25 10:00
 */
@Data
public class PresignedUploadResponse {

    /** 文件id
     */
    private String fileId;

    /** 上传地址 */
    private String url;

    /** 上传凭证（表单直传使用，如七牛 uploadToken） */
    private String token;

    /** 文件在存储桶中的完整 key */
    private String key;

    /** 实际存储的文件名（由后端生成） */
    private String filename;

    /** 原始文件名 */
    private String originalFilename;
}
