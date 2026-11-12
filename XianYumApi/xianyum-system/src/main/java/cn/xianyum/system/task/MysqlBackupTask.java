package cn.xianyum.system.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@JobHandler("mysqlBackupTask")
public class MysqlBackupTask implements IJobHandler {

    private static final String REMOTE_PATH = "mysql/";
    
    // 1MB = 1024 * 1024
    private static final long MB_UNIT = 1024L * 1024L;
    
    @Autowired
    private MessageSender messageSender;

    @Override
    public ReturnT execute(Map<String, String> jobMapParams, SchedulerTool tool) throws Exception {
        String filePath = jobMapParams.get("filePath");
        if (StrUtil.isBlank(filePath)) {
            tool.error("参数 filePath 不能为空");
            return ReturnT.FAILURE;
        }

        File dir = FileUtil.file(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            tool.error("目录不存在: " + filePath);
            return ReturnT.FAILURE;
        }

        tool.info(String.format("源目录:%s,目标路径:%s", dir.getAbsolutePath(), REMOTE_PATH));

        for (File file : FileUtil.loopFiles(dir)) {
            if (!file.isFile()) {
                continue;
            }
            long start = System.currentTimeMillis();

            long fileSizeByte = file.length();
            double fileSizeMb = (double) fileSizeByte / MB_UNIT;
            
            try {
                FileDetailResponse resp = FileUtils.uploadFile(file, REMOTE_PATH);
                long cost = System.currentTimeMillis() - start;
                if (resp != null) {
                    FileUtil.del(file);
                    tool.info(String.format("上传成功, 删除本地文件%s", file.getAbsolutePath()));
                    sendMessage(file.getName(), "success", cost, fileSizeMb);
                } else {
                    tool.error(String.format("上传返回为空: %s", file.getAbsolutePath()));
                    sendMessage(file.getName(), "error", System.currentTimeMillis() - start, fileSizeMb);
                }
            } catch (Exception e) {
                long cost = System.currentTimeMillis() - start;
                tool.error(String.format("上传失败: %s, 原因: %s", file.getAbsolutePath(), e.getMessage()));
                sendMessage(file.getName(), "error", cost, fileSizeMb);
            }
        }

        return ReturnT.SUCCESS;
    }

    /**
     * 发送MySQL备份消息
     * @param fileName
     * @param status
     * @param costMs
     * @param fileSizeMb 文件大小 MB
     */
    private void sendMessage(String fileName, String status, long costMs, double fileSizeMb) {
        try {
            Map<String, Object> content = new LinkedHashMap<>();
            content.put("文件名：", fileName);
            content.put("文件大小：", String.format("%.2f MB", fileSizeMb));
            content.put("上传状态：", status);
            content.put("耗时：", costMs + " ms");
            MessageSenderEntity entity = new MessageSenderEntity();
            entity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
            messageSender.sendAsyncMessage(MessageCodeEnums.MYSQL_BACKUP_TASK_NOTIFY.getMessageCode(), entity);
        } catch (Exception e) {
            log.error("发送MySQL备份消息失败, fileName={}", fileName, e);
        }
    }
}
