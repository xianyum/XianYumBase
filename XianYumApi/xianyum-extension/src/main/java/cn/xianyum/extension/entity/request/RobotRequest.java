package cn.xianyum.extension.entity.request;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025/7/8 22:41
 */
@Data
public class RobotRequest {

    private String content;

    private String groupId;

    private String senderId;

    private String senderNickname;
}
