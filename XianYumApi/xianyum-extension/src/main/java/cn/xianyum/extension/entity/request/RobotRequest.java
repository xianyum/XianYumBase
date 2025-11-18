package cn.xianyum.extension.entity.request;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025/7/8 22:41
 */
@Data
public class RobotRequest {

    private ContentRequest request;

    @Data
    public static class ContentRequest{
        private String content;
    }
}
