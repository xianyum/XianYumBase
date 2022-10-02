package cn.xianyum.common.utils;

import cn.xianyum.common.exception.SoException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author zhangwei
 * @date 2022/5/12 21:29
 */
@Slf4j
public class SchedulerTool {

    private Long jobId;

    private String jobDataDTO;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public SchedulerTool(Long jobId, String jobDataDTO) {
        this.jobId = jobId;
        this.jobDataDTO = jobDataDTO;
    }

    public void debug(String str) {
        this.record(str, "DEBUG");
        log.debug(str);
    }

    public void info(String str) {
        this.record(str, "INFO");
        log.info(str);
    }

    public void warn(String str) {
        this.record(str, "WARN");
        log.warn(str);
    }

    public void error(String str) {
        this.record(str, "ERROR");
        log.error(str);
    }

    private void record(String str, String level) {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            this.byteArrayOutputStream.write((String.format("%s [%5s] : %s", time, level, str) + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new SoException(e.getMessage());
        }
    }


    public void closeLogger() {
        try {
            this.byteArrayOutputStream.flush();
            this.byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new SoException(e.getMessage());
        }
    }


    public Long getJobId() {
        return jobId;
    }

    public String getJobDataDTO() {
        return jobDataDTO;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }
}
