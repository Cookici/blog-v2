/*
 Navicat Premium Dump SQL

 Source Server         : tenxunyun
 Source Server Type    : MySQL
 Source Server Version : 50718 (5.7.18-txsql-log)
 Source Host           : sh-cdb-pezvivda.sql.tencentcdb.com:63950
 Source Schema         : user

 Target Server Type    : MySQL
 Target Server Version : 50718 (5.7.18-txsql-log)
 File Encoding         : 65001

 Date: 11/01/2025 16:35:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `message_content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `user_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `to_user_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `timestamp` bigint(20) UNSIGNED NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_message_id`(`message_id`, `is_deleted`) USING BTREE,
  INDEX `idx_user_to_id`(`user_id`, `to_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
