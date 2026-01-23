package cn.xianyum.common.entity.file;

import lombok.Data;

/**
 * @author xianyum
 * @date 2026/1/23 18:07
 */
@Data
public class FileDetailResponse {

    private String id;

    /** 文件名称 */
    private String filename;

    /** 原始文件名称 */
    private String originalFilename;

    /** 文件名称 */
    private String fileUrl;
}
