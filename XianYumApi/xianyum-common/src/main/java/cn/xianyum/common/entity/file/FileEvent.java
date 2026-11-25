package cn.xianyum.common.entity.file;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

/**
 * 文件事件
 * <p>
 * 各业务模块统一通过 {@link cn.xianyum.common.utils.FileServiceUtils} 发布文件相关事件，
 * 由 system 模块监听并执行具体操作。
 *
 * @author xianyum
 */
public record FileEvent(Operation operation, List<String> fileIds) {

    /**
     * 文件操作类型
     */
    public enum Operation {
        /** 转永久：将上传状态由初始化改为上传完成 */
        TRANSFER_PERMANENT,
        /** 删除文件：删除远程文件及文件记录 */
        DELETE
    }

    public FileEvent {
        if (operation == null) {
            throw new IllegalArgumentException("operation不能为空");
        }
        if (CollUtil.isEmpty(fileIds)) {
            throw new IllegalArgumentException("fileIds不能为空");
        }
        fileIds = List.copyOf(fileIds);
    }
}
