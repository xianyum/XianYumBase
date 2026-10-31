-- =============================================
-- 在线用户表 user_online
-- 对应实体: cn.xianyum.system.entity.po.UserOnlineEntity
-- =============================================
DROP TABLE IF EXISTS `user_online`;
CREATE TABLE `user_online` (
    `id`               VARCHAR(64)   NOT NULL COMMENT '主键（使用 token 作为主键，保证唯一）',
    `token`            VARCHAR(128)  NOT NULL COMMENT '会话 token（业务查询字段）',
    `username`         VARCHAR(64)   DEFAULT NULL COMMENT '用户名',
    `login_time`       DATETIME      DEFAULT NULL COMMENT '登录时间',
    `ipaddr`           VARCHAR(64)   DEFAULT NULL COMMENT '登录IP地址',
    `login_location`   VARCHAR(128)  DEFAULT NULL COMMENT '登录地点',
    `browser`          VARCHAR(64)   DEFAULT NULL COMMENT '浏览器类型',
    `os`               VARCHAR(64)   DEFAULT NULL COMMENT '操作系统',
    `login_type`       VARCHAR(32)   DEFAULT NULL COMMENT '登录来源（账号密码/QQ/支付宝/二维码等）',
    `expire_time`      DATETIME      DEFAULT NULL COMMENT '会话过期时间点',
    `create_by`        VARCHAR(64)   DEFAULT NULL COMMENT '创建人',
    `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
    `create_by_name`   VARCHAR(64)   DEFAULT NULL COMMENT '创建人名称',
    `update_by`        VARCHAR(64)   DEFAULT NULL COMMENT '更新人',
    `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
    `update_by_name`   VARCHAR(64)   DEFAULT NULL COMMENT '更新人名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_username` (`username`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '在线用户表';
