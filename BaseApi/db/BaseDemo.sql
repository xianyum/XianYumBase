/*
 Navicat Premium Data Transfer

 Source Server         : 47.103.20.104_3306
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 47.103.20.104:3306
 Source Schema         : BaseDemo

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 25/05/2019 22:05:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) NULL DEFAULT NULL COMMENT '排序',
  `del_tag` int(2) NULL DEFAULT NULL COMMENT '删除标志（1：已删除）',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, 0, '系统管理', NULL, NULL, 0, 'system', 0, 0);
INSERT INTO `menu` VALUES (2, 1, '管理员列表', 'sys/user', NULL, 1, 'admin', 1, 0);
INSERT INTO `menu` VALUES (3, 1, '角色管理', 'sys/role', NULL, 1, 'role', 2, 0);
INSERT INTO `menu` VALUES (4, 1, '菜单管理', 'sys/menu', NULL, 1, 'menu', 3, 0);
INSERT INTO `menu` VALUES (5, 1, 'SQL监控', 'http://localhost:8080/renren-fast/druid/sql.html', NULL, 1, 'sql', 4, 0);
INSERT INTO `menu` VALUES (6, 1, '定时任务', 'job/schedule', NULL, 1, 'job', 5, 0);
INSERT INTO `menu` VALUES (27, 1, '参数管理', 'sys/config', 'sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete', 1, 'config', 6, 0);
INSERT INTO `menu` VALUES (29, 1, '系统日志', 'sys/log', 'sys:log:list', 1, 'log', 7, 0);
INSERT INTO `menu` VALUES (30, 1, '文件上传', 'oss/oss', 'sys:oss:all', 1, 'oss', 6, 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '用户名',
  `password` varchar(150) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` int(2) NULL DEFAULT NULL COMMENT '状态(0：允许 1：禁止)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_tag` int(2) NOT NULL COMMENT '删除状态（0：未删除，1：删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '5ec2f6053f6de39c238866ce55188df32cc6869974ab968796e744043e9697e9', 'VRYPFK72XVqIKYY7woF2', '15233390065@qq.com', '18815561256', 0, '2019-05-25 20:29:37', 0);
INSERT INTO `user` VALUES (2, 'zhanghua', '6414a837fd64ef24edc00af74e5bc2469c1e15741fc45e5aac331c668f91f491', 'v7xMLpnhu9r7Xh6gTNk9', '1523390065@qq.com', '15512341234', 0, '2019-05-25 21:38:13', 1);

SET FOREIGN_KEY_CHECKS = 1;
