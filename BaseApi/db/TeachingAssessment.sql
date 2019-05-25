/*
 Navicat Premium Data Transfer

 Source Server         : 47.103.20.104_3306
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 47.103.20.104:3306
 Source Schema         : TeachingAssessment

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 20/05/2019 22:04:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for annex_data
-- ----------------------------
DROP TABLE IF EXISTS `annex_data`;
CREATE TABLE `annex_data`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` int(2) NULL DEFAULT NULL COMMENT '考核类型',
  `courseId` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程id',
  `url` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件url',
  `createUserId` int(40) NULL DEFAULT NULL COMMENT '创建id',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `isIdentity` int(2) NULL DEFAULT NULL COMMENT '身份（0：老师上传资料 1:学生提交的作业）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of annex_data
-- ----------------------------
INSERT INTO `annex_data` VALUES ('b05a9a6c7f334a53a639a00ded0ffe88', 1, '4356c36abb9146308de9e63986193865', 'http://47.103.20.104:8000/file/3dd7e6a493fb43e0a6402d0f64264fc1.doc', 5, '2019-05-20 01:09:42', 0);
INSERT INTO `annex_data` VALUES ('c228920416f444288ab49206bea6214a', 0, '4356c36abb9146308de9e63986193865', 'http://47.103.20.104:8000/file/771ec58ff06f4f11a66687bcf972dff1.doc', 5, '2019-05-20 01:08:45', 0);

-- ----------------------------
-- Table structure for captcha
-- ----------------------------
DROP TABLE IF EXISTS `captcha`;
CREATE TABLE `captcha`  (
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证码',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of captcha
-- ----------------------------
INSERT INTO `captcha` VALUES ('1220e0de-766c-4fb8-867a-f135ab73cc2b', '7f2xb', '2019-04-24 22:44:06');
INSERT INTO `captcha` VALUES ('1709d0e8-956d-47a6-8620-e7465effb7b5', 'f7nxc', '2019-04-28 15:10:42');
INSERT INTO `captcha` VALUES ('19bd69be-d81c-473a-886f-b20d05f9b225', 'd22nn', '2019-04-27 15:24:46');
INSERT INTO `captcha` VALUES ('1d2abf69-455a-404f-81d3-4b26d4d3d1', '855w4', '2019-04-27 21:01:48');
INSERT INTO `captcha` VALUES ('1d2abf69-455a-404f-81d3-4b26d5364d3d', '8y4pw', '2019-04-27 21:01:32');
INSERT INTO `captcha` VALUES ('28d43f83-470c-465f-8a9a-426b57a31e61', 'mww7p', '2019-04-21 22:36:08');
INSERT INTO `captcha` VALUES ('2ac50851-9bac-4e82-84cb-336a11bb0dcb', '7y433', '2019-04-23 00:38:19');
INSERT INTO `captcha` VALUES ('2f084b1c-5358-47bd-8a06-cba04390c544', 'an2b7', '2019-04-27 15:14:42');
INSERT INTO `captcha` VALUES ('3131', '23nx4', '2019-04-25 14:32:09');
INSERT INTO `captcha` VALUES ('31311331', 'd74g7', '2019-04-25 14:56:58');
INSERT INTO `captcha` VALUES ('313113311', '2px3n', '2019-04-25 14:57:25');
INSERT INTO `captcha` VALUES ('313113311131', 'p757y', '2019-04-25 14:59:04');
INSERT INTO `captcha` VALUES ('3131133111313131', 'n8eyn', '2019-04-25 15:01:37');
INSERT INTO `captcha` VALUES ('31311331113131313131', 'bex4a', '2019-04-25 15:03:31');
INSERT INTO `captcha` VALUES ('313131', 'gxn6m', '2019-04-25 14:30:54');
INSERT INTO `captcha` VALUES ('3131313', 'ne7em', '2019-04-25 14:31:51');
INSERT INTO `captcha` VALUES ('3131313131313131', '5n556', '2019-04-25 14:39:23');
INSERT INTO `captcha` VALUES ('31313131313131313131', '24db7', '2019-04-25 14:40:53');
INSERT INTO `captcha` VALUES ('313131313131313133131', '66g6a', '2019-04-25 14:41:31');
INSERT INTO `captcha` VALUES ('3131313131313131331313', 'na836', '2019-04-25 14:42:01');
INSERT INTO `captcha` VALUES ('313131313131313133131331', 'y26be', '2019-04-25 14:55:27');
INSERT INTO `captcha` VALUES ('347ac720-1c50-4611-8dea-a2c0ec65d518', 'x3f5n', '2019-04-27 19:20:31');
INSERT INTO `captcha` VALUES ('34e015e2-5d59-420d-8e5f-d8d6b43e4168', 'dpnew', '2019-05-13 23:09:20');
INSERT INTO `captcha` VALUES ('361e7289-b0ea-44f1-853a-c7351b38f06d', 'eanw7', '2019-04-28 15:00:23');
INSERT INTO `captcha` VALUES ('3d5b2039-af24-4a4c-8cdf-979609bf050e', 'nnnep', '2019-05-13 23:11:01');
INSERT INTO `captcha` VALUES ('3ecf9a72-527e-4b47-8155-7d80d02901f4', '782gm', '2019-04-27 19:21:10');
INSERT INTO `captcha` VALUES ('4564789', 'manep', '2019-04-27 20:29:26');
INSERT INTO `captcha` VALUES ('4564789756', 'enxpd', '2019-04-27 20:29:39');
INSERT INTO `captcha` VALUES ('4567981', 'x6w32', '2019-04-27 21:08:55');
INSERT INTO `captcha` VALUES ('5281de23-8450-4872-896b-28f40dc91704', 'g6nc7', '2019-04-27 19:47:12');
INSERT INTO `captcha` VALUES ('58af2e6d-264f-4a95-86e3-f50c132344c3', 'fwc6d', '2019-04-27 20:14:16');
INSERT INTO `captcha` VALUES ('59385154-2a07-413d-8fb8-c2181b096af3', 'pap4n', '2019-04-28 15:06:02');
INSERT INTO `captcha` VALUES ('67f4c102-e21d-4f25-84f3-bb3353364fa6', 'ydgcb', '2019-05-13 23:11:34');
INSERT INTO `captcha` VALUES ('67fdceb1-afd0-40ca-8a2e-084c10b50390', 'nc28e', '2019-04-22 19:32:31');
INSERT INTO `captcha` VALUES ('7d34bd4d-9129-45c5-8d2d-d3034b96c50d', 'ne4m7', '2019-04-28 15:05:45');
INSERT INTO `captcha` VALUES ('80bc7226-24af-4f85-8a72-68e1d92818e7', 'yxbwc', '2019-04-27 20:52:58');
INSERT INTO `captcha` VALUES ('8a409b5a-1369-4355-8fae-f4d613b4ae82', '8g736', '2019-04-27 17:22:06');
INSERT INTO `captcha` VALUES ('8e1b1ddf-e9f4-4060-83ce-3616e445090d', '54dep', '2019-04-28 15:06:19');
INSERT INTO `captcha` VALUES ('8ea7d7d9-219e-424d-86b6-d9f334e2f94f', 'ncfg7', '2019-04-27 14:45:13');
INSERT INTO `captcha` VALUES ('987654321', 'p6d32', '2019-04-21 22:25:57');
INSERT INTO `captcha` VALUES ('9876543211', 'd7d5a', '2019-04-21 22:26:38');
INSERT INTO `captcha` VALUES ('98765432112', 'yn8pc', '2019-04-21 22:26:49');
INSERT INTO `captcha` VALUES ('9d1fa569-69d8-40d9-81d7-f4b508134743', 'x6746', '2019-04-22 20:54:45');
INSERT INTO `captcha` VALUES ('a1d05f69-3406-4716-819c-dc9dd5837259', 'ea3de', '2019-04-27 19:14:19');
INSERT INTO `captcha` VALUES ('a8e3a3b0-38f5-41e6-83d5-c9ef34249b47', 'nn7w3', '2019-04-22 20:54:24');
INSERT INTO `captcha` VALUES ('ad2d8624-fb79-4813-8ac3-71e8c3f49ee9', 'wgbe8', '2019-04-21 22:36:20');
INSERT INTO `captcha` VALUES ('aefc62af-a0c9-458c-8e38-f552c5893d05', 'fbwwn', '2019-04-27 19:21:12');
INSERT INTO `captcha` VALUES ('b13401d7-4c3c-4c53-8809-682804333eea', 'em5fx', '2019-04-27 19:45:02');
INSERT INTO `captcha` VALUES ('b25c4f41-91af-4239-81d0-b2c595301832', 'gnafy', '2019-04-27 19:22:51');
INSERT INTO `captcha` VALUES ('b43387f6-678f-4f0c-85b8-3a9216862b95', 'p6bp2', '2019-04-27 15:12:42');
INSERT INTO `captcha` VALUES ('b8e68c0c-1450-4433-8425-c5f0b03ca476', 'bnebe', '2019-04-27 19:22:51');
INSERT INTO `captcha` VALUES ('bbdf4b8c-a58a-48cb-8199-825f78010a22', 'ngxg4', '2019-04-27 15:15:20');
INSERT INTO `captcha` VALUES ('c97864fa-530a-427c-8f1c-a2419391fd70', '524yy', '2019-04-27 14:18:47');
INSERT INTO `captcha` VALUES ('d00c9e36-a4f2-48e2-89df-6c653ce293a4', 'yn7d8', '2019-04-27 14:43:20');
INSERT INTO `captcha` VALUES ('d3efed1e-f5cf-426d-898f-ae9a5b929a2c', 'xpf2e', '2019-04-27 15:13:48');
INSERT INTO `captcha` VALUES ('ee4cd8af-c1ad-4213-89b0-b0c8b5ce15f0', '6ybfp', '2019-04-27 14:23:25');
INSERT INTO `captcha` VALUES ('f2647318-ee8f-4837-8085-fda3c1852022', '5fyb6', '2019-05-13 23:09:23');
INSERT INTO `captcha` VALUES ('f7e4a460-a889-4de6-86f3-cbd52a1414b1', '4eanx', '2019-04-27 15:25:30');
INSERT INTO `captcha` VALUES ('ff5bb6f7-8bf7-446c-849b-8894c1d8a99f', '464p6', '2019-04-28 15:00:31');
INSERT INTO `captcha` VALUES ('ff9ded9e-b8a5-4a8d-8829-e2057bcb290e', 'fp8ny', '2019-05-12 15:04:51');

-- ----------------------------
-- Table structure for comprehensive_results
-- ----------------------------
DROP TABLE IF EXISTS `comprehensive_results`;
CREATE TABLE `comprehensive_results`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `studentId` int(20) NULL DEFAULT NULL COMMENT '学生id',
  `studentName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学生姓名',
  `courseId` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程id',
  `courseName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `teacherId` int(40) NULL DEFAULT NULL COMMENT '教师id',
  `teacherName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '教师名称',
  `homeworkScore` double NULL DEFAULT NULL COMMENT '课后作业分数',
  `phaseTestingScore` double NULL DEFAULT NULL COMMENT '阶段性测试分数',
  `experimentScore` double NULL DEFAULT NULL COMMENT '实验打分',
  `classroomPerformanceScore` double NULL DEFAULT NULL COMMENT '课堂表现分数',
  `finalExamScore` double NULL DEFAULT NULL COMMENT '期末考试分数',
  `systemScore` double NULL DEFAULT NULL COMMENT '系统评定成绩',
  `systemTime` datetime(0) NULL DEFAULT NULL COMMENT '系统评定时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comprehensive_results
-- ----------------------------
INSERT INTO `comprehensive_results` VALUES ('0d64e98d810043e6a0f7ec890b7ad2ae', 7, '13131', '4356c36abb9146308de9e63986193865', 'c语言开发', 5, '王老师', 0, 1231, 9.5, 131456, 15, 13276.6, '2019-05-19 22:28:00');
INSERT INTO `comprehensive_results` VALUES ('3a03ec09acd343f69da0b171e2c7eb44', 9, '王晓楠', '3aab9468d17245b7b0e4f64db3d66c67', '大家来加入这个课程把', 8, '张伟', 0, 88.6, 55.5, 13.5, 50, 20.759999999999998, '2019-05-19 22:28:00');
INSERT INTO `comprehensive_results` VALUES ('b1125c570bb14e9e9ea243e09f4ab1ff', 7, '13131', '3aab9468d17245b7b0e4f64db3d66c67', '大家来加入这个课程把', 8, '张伟', 0, 0, 0, 0, 0, 0, '2019-05-19 22:28:00');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `courseTitle` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `courseContent` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程内容',
  `courseCode` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程邀请码',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(40) NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `courseCode`(`courseCode`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES ('3aab9468d17245b7b0e4f64db3d66c67', '大家来加入这个课程把', '大家来加入这个课程把', '6ba84dab-aa35-4dce-84ef-a321f187f04f', '2019-05-14 23:09:56', 8);
INSERT INTO `course` VALUES ('4356c36abb9146308de9e63986193865', 'c语言开发', '感兴趣得同学加入', 'cb7aee46-78ae-4825-82a8-35fa0ce0ea1c', '2019-05-13 22:47:41', 5);

-- ----------------------------
-- Table structure for course_user
-- ----------------------------
DROP TABLE IF EXISTS `course_user`;
CREATE TABLE `course_user`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `userId` int(40) NULL DEFAULT NULL COMMENT '学生id',
  `courseId` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程id',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_user
-- ----------------------------
INSERT INTO `course_user` VALUES ('0e4ef345d07641d69ffb77e77d0183dc', 7, '4356c36abb9146308de9e63986193865', '2019-05-13 22:48:08');
INSERT INTO `course_user` VALUES ('6b12dd39ee4842da9d612adb79b93966', 8, '3aab9468d17245b7b0e4f64db3d66c67', '2019-05-14 23:10:28');
INSERT INTO `course_user` VALUES ('d95f62c08da64ca6be4758b5c2589a6c', 9, '3aab9468d17245b7b0e4f64db3d66c67', '2019-05-14 23:10:13');

-- ----------------------------
-- Table structure for general_score
-- ----------------------------
DROP TABLE IF EXISTS `general_score`;
CREATE TABLE `general_score`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `studentId` int(40) NULL DEFAULT NULL COMMENT '学生id',
  `score` double NULL DEFAULT NULL COMMENT '分数',
  `type` int(2) NULL DEFAULT NULL COMMENT '考核类型',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(40) NULL DEFAULT NULL COMMENT '创建人ID',
  `courseId` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of general_score
-- ----------------------------
INSERT INTO `general_score` VALUES ('023f23a71f1d4919b914d9d00dfadf2a', 9, 13.5, 3, '2019-05-14 23:11:34', 8, '3aab9468d17245b7b0e4f64db3d66c67');
INSERT INTO `general_score` VALUES ('0acdcab0755b4eabac51d56e4bbbfbdd', 7, 1231, 1, '2019-05-13 23:50:34', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('11d2b7a88823469bb0dc5d9fd5f72c2b', 9, 88.6, 1, '2019-05-14 23:11:17', 8, '3aab9468d17245b7b0e4f64db3d66c67');
INSERT INTO `general_score` VALUES ('1496bc94656b4955a020aba37fac1234', 7, 15, 4, '2019-05-13 23:50:21', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('533f38cdaa454827bf8447111e31f2c7', 9, 50, 4, '2019-05-14 23:11:42', 8, '3aab9468d17245b7b0e4f64db3d66c67');
INSERT INTO `general_score` VALUES ('67c6e370cf4f46c08b41174c657aa105', NULL, NULL, 1, '2019-05-19 23:25:42', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('68f1fa54c8424f40b6da75e6742a2259', NULL, NULL, 1, '2019-05-19 23:24:54', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('9e58a7089a514219b99cd22dc6d83da8', 9, 55.5, 2, '2019-05-14 23:11:03', 8, '3aab9468d17245b7b0e4f64db3d66c67');
INSERT INTO `general_score` VALUES ('b95ba443aee14f35aced07205df37d29', 7, 131456, 3, '2019-05-13 23:50:01', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('eae96ad26c21462426108a2', 7, 9.5, 2, '2019-05-13 22:51:14', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('eae96ad26c214678a4e157b99a6108a2', 7, 8.5, 2, '2019-05-13 22:51:14', 5, '4356c36abb9146308de9e63986193865');
INSERT INTO `general_score` VALUES ('eae96ad26c2241462426108a2', 7, 10.5, 2, '2019-05-13 22:51:14', 5, '4356c36abb9146308de9e63986193865');

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
  `del_tag` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单管理' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, 0, '用户管理', 'sys/user', 'admin', 0, 'admin', 0, 0);
INSERT INTO `menu` VALUES (2, 0, '个人信息', 'sys/info', 'teacher;student', 0, 'zonghe', 0, 0);
INSERT INTO `menu` VALUES (3, 0, '公告管理', 'sys/notice', 'teacher;student', 0, 'tixing', 0, 0);
INSERT INTO `menu` VALUES (4, 0, '附件资料', 'sys/data', 'teacher;student', 0, 'oss', 0, 0);
INSERT INTO `menu` VALUES (5, 0, '课程信息', 'sys/course', 'teacher;student', 0, 'mudedi', 0, 0);
INSERT INTO `menu` VALUES (6, 0, '课后作业', 'sys/homework', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (7, 0, '阶段性测试', 'sys/phaseTestingRatio', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (8, 0, '课程实验', 'sys/experimentRatio', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (9, 0, '课堂表现', 'sys/classroomPerformance', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (10, 0, '期末成绩', 'sys/finalExamRatio', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (11, 0, '成绩综合评定', 'sys/result', 'teacher;student', 0, 'sql', 0, 0);
INSERT INTO `menu` VALUES (14, 0, '权重占比', 'sys/weightRatio', 'teacher', 0, 'zhedie', 0, 0);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `noticeTitle` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公告主题',
  `noticeContent` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公告内容',
  `createUserId` int(40) NULL DEFAULT NULL COMMENT '创建人id',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES ('577b480b25a54aec8cf34114fe88a25e', '明天不上课', '由于老师身体不适，明天不上课 ', 5, '2019-05-20 21:26:30');
INSERT INTO `notice` VALUES ('fbbcc2f28f794e42bc3ccc73779d317c', '57456456', 'zwewerw', 5, '2019-05-20 21:19:37');

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
  `mobile` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` int(2) NULL DEFAULT NULL COMMENT '状态(0：允许 1：禁止)',
  `permission_status` int(2) NULL DEFAULT NULL COMMENT '权限状态',
  `permission_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户权限标识符',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_tag` int(2) NOT NULL COMMENT '删除状态（0：未删除，1：删除）',
  `nick_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` int(2) NULL DEFAULT NULL COMMENT '性别',
  `department` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '院系',
  `major` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专业',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '210598c4798e3947d5a016c5098c8a12281aeaafe0f8122fa106f6022c497232', '30V7xl03zWmqlQLzS4I1', '1523390065@qq.com', '13152011314', 0, 0, 'admin', '2019-04-18 13:38:39', 0, '张筱雨', 0, '智能科学学院', '计算机科学与技术');
INSERT INTO `user` VALUES (5, 'zhangwei1', 'bba1c3b580b279bab49bda2eb725bc88af18013b5fb576a7abd859b0f79ae08a', 'BfU9q7u5bYwQAa8G566x', '80616059@qq.com', '15512341234', 0, 1, 'teacher', '2019-05-12 17:11:29', 0, '王老师', 0, '智能科学信息技术', '会计学');
INSERT INTO `user` VALUES (7, 'student1', 'b90e910d1d63536d66ff4a356c8a18907c24d51a5de8f9953038682a5d8e290f', 'rUBt0NAcaDaHuUAiPP77', '131311', '13131', 0, 2, 'student', '2019-05-12 20:03:48', 0, '13131', 0, '3131', '131');
INSERT INTO `user` VALUES (8, 'zhangwei2', 'fde5e14981fed089982fd59766a53bda21eebf5ae9239c4769816f52a4da23e7', '2hHTbXKEhL2n4y29fkwR', '13131@qq.com', '13', 0, 1, 'teacher', '2019-05-14 23:06:54', 0, '张伟', 0, '131', '131');
INSERT INTO `user` VALUES (9, 'student2', '679e175b47d6d82611a49c19a7e0c5f6cfa618f7383f1c3be5af8703a73c792d', 'zxGlZTXCqAL01BaVnYM0', '112345645@qq.com', '15512341234', 0, 2, 'student', '2019-05-14 23:07:05', 0, '王晓楠', 0, '建筑学院', '建筑力道');

-- ----------------------------
-- Table structure for user_token
-- ----------------------------
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token`  (
  `user_id` int(11) NOT NULL,
  `token` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'token',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_token
-- ----------------------------
INSERT INTO `user_token` VALUES (1, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UZzBNRFF3Tnpjc0luVnpaWEpKWkNJNklqRWlmUS5HR1pWb1VHd0h1cEtTSHF4YWhjYl9EOEtScU1kLXFadkN6cWVoSjF3V1NB', NULL, NULL);
INSERT INTO `user_token` VALUES (2, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UWTBNVFEwTURNc0luVnpaWEpKWkNJNklqSWlmUS5IajUxandIZmRUbklUZ0JKT25pTmFSSWNkS21rbmViaU5NSFM2RTJUTlNR', '2019-04-28 09:20:03', '2019-04-27 21:20:03');
INSERT INTO `user_token` VALUES (3, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UWTBNVEF5T0RNc0luVnpaWEpKWkNJNklqTWlmUS55WVJxMi1wQzlWS3dRY2pLVU1CdFZMcEV5b3N1OGZCcFVNMGlRSDNVdktF', NULL, NULL);
INSERT INTO `user_token` VALUES (5, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UZzBNRFF3T0RRc0luVnpaWEpKWkNJNklqVWlmUS5QeE5OODUtTDB3Ym1UX1NjRFRjZVlmQVBjUVBfZkdSaEhYVWEwSk9uSUFB', '2019-05-21 10:01:24', '2019-05-20 22:01:24');
INSERT INTO `user_token` VALUES (7, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UZzBNRE0zTkRBc0luVnpaWEpKWkNJNklqY2lmUS5fRmwwUVNmaGkyV0c2MjZnMGtQYzhwNElhWFQ2b1NOa1dwTDVNMDNYU2dn', '2019-05-21 09:55:40', '2019-05-20 21:55:40');
INSERT INTO `user_token` VALUES (8, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UZ3pNamd3TkRjc0luVnpaWEpKWkNJNklqZ2lmUS5ZeDJBOVBfSkpQSWpnVG9OenYzX3FaNHhCMDF2cjhIR0tPMlhOdjV0YUln', NULL, NULL);
INSERT INTO `user_token` VALUES (9, 'ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5UYzRPRGs0TVRnc0luVnpaWEpKWkNJNklqa2lmUS5vcU1WMGMxYVVWSGFmOWdOeXo5bFY1eGRiQnA4UWRRMjlDUm9fWW81V29J', NULL, NULL);

-- ----------------------------
-- Table structure for weight_ratio
-- ----------------------------
DROP TABLE IF EXISTS `weight_ratio`;
CREATE TABLE `weight_ratio`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teacherId` int(40) NULL DEFAULT NULL COMMENT '关联教师id',
  `homeworkRatio` double NULL DEFAULT NULL COMMENT '课后作业系数',
  `phaseTestingRatio` double NULL DEFAULT NULL COMMENT '阶段性测试系数',
  `experimentRatio` double NULL DEFAULT NULL COMMENT '实验打分系数',
  `finalExamRatio` double NULL DEFAULT NULL COMMENT '期末考试成绩系数',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `classroomPerformance` double NULL DEFAULT NULL COMMENT '课堂表现系数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weight_ratio
-- ----------------------------
INSERT INTO `weight_ratio` VALUES ('13ffead7cdc44110b0657a6be09cc5a2', 5, 0.2, 0.1, 0.2, 0.4, '2019-05-15 00:08:27', 0.1);
INSERT INTO `weight_ratio` VALUES ('417f83394d254c0c95ae00fe26ed9f5b', 8, 0.6, 0.1, 0.1, 0.1, '2019-05-15 00:08:47', 0.1);

SET FOREIGN_KEY_CHECKS = 1;
