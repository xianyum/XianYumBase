package cn.xianyum.system.infra.event;

import cn.xianyum.common.entity.file.FileEvent;
import cn.xianyum.system.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 文件事件监听器
 * <p>
 * 在业务事务提交后执行，避免业务回滚但文件已被处理。
 *
 * @author xianyum
 */
@Slf4j
@Component
public class FileEventListener {

    @Resource
    private FileService fileService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleFileEvent(FileEvent event) {
        switch (event.operation()) {
            case TRANSFER_PERMANENT -> fileService.updateUploadStatusToComplete(event.fileIds());
            case DELETE -> fileService.deleteFiles(event.fileIds());
        }
    }
}
