package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.response.RobotResponse;

/**
 * @author zhangwei
 * @date 2025/7/8 23:09
 */
public interface RobotService {

    RobotResponse autoReply(String content);
}
