package cn.xianyum.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.entity.file.FileEvent;

import java.util.List;

/**
 * 文件服务工具类
 *
 * @author xianyum
 */
public class FileServiceUtils {

    private FileServiceUtils() {
    }

    /**
     * 文件转永久：将直传文件的记录状态由初始化更新为上传完成
     *
     * @param fileIds 文件id集合
     */
    public static void transferPermanent(List<String> fileIds) {
        publish(FileEvent.Operation.TRANSFER_PERMANENT, fileIds);
    }

    /**
     * 删除文件：删除远程文件及文件记录
     *
     * @param fileIds 文件id集合
     */
    public static void deleteFiles(List<String> fileIds) {
        publish(FileEvent.Operation.DELETE, fileIds);
    }

    /**
     * 发布文件事件
     */
    private static void publish(FileEvent.Operation operation, List<String> fileIds) {
        if (CollUtil.isEmpty(fileIds)) {
            return;
        }
        SpringUtil.publishEvent(new FileEvent(operation, fileIds));
    }
}
