package cn.xianyum.system.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.annotation.JobHandler;
import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.handler.IJobHandler;
import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.common.utils.SchedulerTool;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.Map;

@Slf4j
@JobHandler("mysqlBackupTask")
public class MysqlBackupTask implements IJobHandler {

    private static final String REMOTE_PATH = "mysql/";

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
            try {
                FileDetailResponse resp = FileUtils.uploadFile(file, REMOTE_PATH);
                if (resp != null) {
                    FileUtil.del(file);
                    tool.info(String.format("上传成功, 删除本地文件%s", file.getAbsolutePath()));
                } else {
                    tool.error(String.format("上传返回为空: %s", file.getAbsolutePath()));
                }
            } catch (Exception e) {
                tool.error(String.format("上传失败: %s, 原因: %s", file.getAbsolutePath(), e.getMessage()));
            }
        }

        return ReturnT.SUCCESS;
    }
}
