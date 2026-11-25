package cn.xianyum.system.service;


import cn.xianyum.system.entity.po.FileDetailEntity;
import org.dromara.x.file.storage.core.FileInfo;

import java.util.List;


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

    /**
     * 通过ID列表查询文件
     * @param fileIds
     * @return
     */
    List<FileDetailEntity> selectByFileIds(List<String> fileIds);

    /**
     * 将 FileDetail 转为 FileInfo
     */
    FileInfo toFileInfo(FileDetailEntity detail);

    void deleteById(String id);

    /**
     * 更新文件上传状态为完成
     */
    void updateComplete(FileDetailEntity fileDetail);
}
