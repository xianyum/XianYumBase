package cn.xianyum.system.service;


import org.dromara.x.file.storage.core.FileInfo;

/**
 * 文件记录表(FileDetail)service层
 *
 * @author zhangwei
 * @since 2026-01-19 22:20:15
 */
public interface FileDetailService{

    /**
     * 通过ID查询文件
     * @param fileId
     * @return
     */
    FileInfo selectFileById(String fileId);
}
