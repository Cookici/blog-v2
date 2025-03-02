/*
 Navicat Premium Dump SQL

 Source Server         : 腾讯云-blog
 Source Server Type    : MySQL
 Source Server Version : 50718 (5.7.18-txsql-log)
 Source Host           : sh-cdb-pezvivda.sql.tencentcdb.com:63950
 Source Schema         : article

 Target Server Type    : MySQL
 Target Server Version : 50718 (5.7.18-txsql-log)
 File Encoding         : 65001

 Date: 02/03/2025 18:23:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_label
-- ----------------------------
DROP TABLE IF EXISTS `t_label`;
CREATE TABLE `t_label`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `label_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `label_alias` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `label_description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_label_id`(`label_id`, `is_deleted`) USING BTREE,
  INDEX `uk_label_name`(`label_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
