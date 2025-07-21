/*
 Navicat Premium Dump SQL

 Source Server         : nikke
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : acg_resource

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 21/07/2025 15:56:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作模块',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户代理',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资源描述',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源链接',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源图标URL',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签，逗号分隔',
  `view_count` bigint NOT NULL DEFAULT 0 COMMENT '访问次数',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重，越大越靠前',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `last_check_time` datetime NULL DEFAULT NULL COMMENT '最近检测时间',
  `check_fail_count` int NULL DEFAULT 0 COMMENT '连续检测失败次数',
  `last_check_status` int NULL DEFAULT 1 COMMENT '最近检测结果 1-可用 0-异常',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  CONSTRAINT `resource_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `resource_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `resource_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource
-- ----------------------------
INSERT INTO `resource` VALUES (1, '哔哩哔哩', '中国领先的年轻人文化社区和视频平台', 'https://www.bilibili.com', 'fas fa-play-circle', 2, '视频,动漫,娱乐', 1031, 500, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 21:10:50', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (2, 'AGE动漫', '免费在线观看动漫的网站', 'http://www.age.tv/', 'fas fa-tv', 2, '动漫,在线观看', 806, 300, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 20:43:25', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (3, '动漫花园', '动漫资源下载网站', 'https://share.dmhy.org', 'fas fa-download', 3, '动漫,下载,BT', 623, 200, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 12:49:15', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (4, '爱奇艺', '中国高品质视频娱乐服务提供商', 'https://www.iqiyi.com', 'fas fa-play', 6, '视频,影视,娱乐', 1204, 600, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-21 15:49:07', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (5, '网易云音乐', '专注于发现与分享的音乐平台', 'https://music.163.com', 'fas fa-music', 5, '音乐,娱乐,在线', 912, 400, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 21:04:19', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (6, 'GitHub', '全球最大的代码托管平台', 'https://github.com', 'fas fa-code', 6, '编程,开源,工具', 1523, 800, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 21:04:58', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (7, 'Pixiv', '日本插画社交网站', 'https://www.pixiv.net', 'fas fa-palette', 13, '插画,二次元,艺术', 700, 350, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-21 15:02:40', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (8, '漫画DB', '在线漫画阅读网站', 'https://www.manhuadb.com', 'fas fa-book-open', 8, '漫画,在线阅读', 509, 250, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-21 15:44:49', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (9, 'Steam', '全球最大的数字游戏发行平台', 'https://store.steampowered.com', 'fas fa-gamepad', 9, '游戏,数字发行', 1107, 550, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-21 15:47:11', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (10, 'NGA论坛', '一些实用软件', 'https://bbs.nga.cn', 'fas fa-comments', 10, '论坛,游戏,社区', 400, 150, 1, 0, 1, '2025-07-13 20:28:55', '2025-07-14 14:37:09', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (11, '次元城动画（推荐）', '次元城动画', 'https://www.ciyuancheng.net/', '', 2, '', 24, 0, 1, 100, 1, '2025-07-14 19:19:26', '2025-07-14 21:01:00', 0, NULL, 0, 1);
INSERT INTO `resource` VALUES (12, 'AcFun', 'AcFun弹幕视频网', 'https://www.acfun.cn/', '', 2, '', 1, 0, 1, 0, 1, '2025-07-14 20:26:27', '2025-07-14 20:26:43', 0, NULL, 0, 1);

-- ----------------------------
-- Table structure for resource_category
-- ----------------------------
DROP TABLE IF EXISTS `resource_category`;
CREATE TABLE `resource_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类代码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '资源分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource_category
-- ----------------------------
INSERT INTO `resource_category` VALUES (1, '动漫专区', 'anime', '动漫相关资源', 'fas fa-tv', 1, 0, 1, '2025-07-13 20:28:55', '2025-07-13 20:28:55', 0);
INSERT INTO `resource_category` VALUES (2, '在线动漫', 'anime-online', '在线观看动漫', 'fas fa-play', 1, 1, 1, '2025-07-13 20:28:55', '2025-07-13 20:28:55', 0);
INSERT INTO `resource_category` VALUES (3, '动漫下载', 'anime-download', '动漫下载资源', 'fas fa-download', 2, 1, 1, '2025-07-13 20:28:55', '2025-07-13 20:28:55', 0);
INSERT INTO `resource_category` VALUES (4, '影音专区', 'audio-video', '影视音乐资源', 'fas fa-video', 2, 0, 1, '2025-07-13 20:28:55', '2025-07-21 15:51:11', 0);
INSERT INTO `resource_category` VALUES (5, '音乐平台', 'music-platform', '视频平台资源', 'fas fa-music', 3, 4, 1, '2025-07-13 20:28:55', '2025-07-21 15:50:24', 0);
INSERT INTO `resource_category` VALUES (6, '在线影视', 'video-online', '在线观看影视', 'fas fa-tools', 4, 4, 1, '2025-07-13 20:28:55', '2025-07-14 01:05:30', 0);
INSERT INTO `resource_category` VALUES (7, '娱乐导航', 'images', '娱乐导航资源', 'fas fa-images', 5, 0, 1, '2025-07-13 20:28:55', '2025-07-14 14:49:00', 0);
INSERT INTO `resource_category` VALUES (8, '游戏平台', 'manga', '热门游戏平台', 'fas fa-book', 6, 7, 1, '2025-07-13 20:28:55', '2025-07-14 14:49:19', 0);
INSERT INTO `resource_category` VALUES (9, '游戏工具', 'game', '一些游戏工具', 'fas fa-gamepad', 7, 7, 1, '2025-07-13 20:28:55', '2025-07-14 14:49:44', 0);
INSERT INTO `resource_category` VALUES (10, '实用软件', 'community', '实用软件资源', 'fas fa-users', 8, 0, 1, '2025-07-13 20:28:55', '2025-07-14 14:49:55', 0);
INSERT INTO `resource_category` VALUES (11, '软件推荐', 'asmr', '软件推荐', 'fas fa-headphones', 9, 10, 1, '2025-07-13 20:28:55', '2025-07-14 14:50:03', 0);
INSERT INTO `resource_category` VALUES (12, '装机必备', 'cloud', '装机必备', 'fas fa-cloud', 10, 10, 1, '2025-07-13 20:28:55', '2025-07-14 14:50:08', 0);
INSERT INTO `resource_category` VALUES (13, '动漫美图', 'anime-picture', '一些动漫美图', '', 1, 1, 1, '2025-07-21 15:02:05', '2025-07-21 15:02:05', 0);
INSERT INTO `resource_category` VALUES (14, '学习专区', 'study', '学习相关网站', '', 0, 0, 1, '2025-07-21 15:55:07', '2025-07-21 15:55:07', 0);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'site_name', 'ACG资源管理平台', '网站名称', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (2, 'site_description', '一个专注于ACG资源分享和管理的平台', '网站描述', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (3, 'site_keywords', 'ACG,动漫,资源,分享,管理', '网站关键词', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (4, 'site_logo', '/logo.png', '网站Logo', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (5, 'site_favicon', '/favicon.ico', '网站图标', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (6, 'max_upload_size', '10485760', '最大上传文件大小（字节）', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (7, 'allowed_file_types', 'jpg,jpeg,png,gif,mp4,avi,mov,zip,rar', '允许上传的文件类型', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (8, 'enable_registration', 'true', '是否允许用户注册', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (9, 'enable_comment', 'true', '是否允许评论', '2025-07-13 20:28:55', '2025-07-13 20:28:55');
INSERT INTO `system_config` VALUES (10, 'enable_rating', 'true', '是否允许评分', '2025-07-13 20:28:55', '2025-07-13 20:28:55');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin@acg.com', '$2a$10$nIX1C54RaQderWSFEdTWreE7D1RLREjL.7cC8EZ5C7jGH2YKbH1Ty', '管理员', 'https://hirihiri.oss-cn-nanjing.aliyuncs.com/QQ%E6%88%AA%E5%9B%BE20230716003445.png', 'ADMIN', 1, '2025-07-21 14:08:13', '2025-07-13 20:28:55', '2025-07-21 14:08:13', 0);
INSERT INTO `user` VALUES (2, 'hiiro', '2353539668@qq.com', '$2a$10$TPjNO7zkrU7xar.EBnlHSOZtJTycbvyPgYkiET6BRu4sWpr6/f3pu', 'hiiro', NULL, 'USER', 1, '2025-07-14 17:34:23', '2025-07-14 04:38:14', '2025-07-14 17:34:23', 0);

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_resource`(`user_id` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `resource_id`(`resource_id` ASC) USING BTREE,
  CONSTRAINT `user_favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_favorite_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------

-- ----------------------------
-- Table structure for user_history
-- ----------------------------
DROP TABLE IF EXISTS `user_history`;
CREATE TABLE `user_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_visit_time`(`visit_time` ASC) USING BTREE,
  INDEX `resource_id`(`resource_id` ASC) USING BTREE,
  CONSTRAINT `user_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_history_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_history
-- ----------------------------

-- ----------------------------
-- View structure for v_hot_resources
-- ----------------------------
DROP VIEW IF EXISTS `v_hot_resources`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_hot_resources` AS select `r`.`id` AS `id`,`r`.`name` AS `name`,`r`.`description` AS `description`,`r`.`url` AS `url`,`r`.`icon` AS `icon`,`r`.`category_id` AS `category_id`,`rc`.`name` AS `category_name`,`r`.`view_count` AS `view_count`,`r`.`like_count` AS `like_count`,`r`.`created_time` AS `created_time` from (`resource` `r` left join `resource_category` `rc` on((`r`.`category_id` = `rc`.`id`))) where ((`r`.`deleted` = 0) and (`r`.`status` = 1)) order by `r`.`view_count` desc,`r`.`like_count` desc;

-- ----------------------------
-- View structure for v_resource_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_resource_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_resource_stats` AS select `r`.`id` AS `id`,`r`.`name` AS `name`,`r`.`description` AS `description`,`r`.`url` AS `url`,`r`.`icon` AS `icon`,`r`.`category_id` AS `category_id`,`rc`.`name` AS `category_name`,`r`.`tags` AS `tags`,`r`.`view_count` AS `view_count`,`r`.`like_count` AS `like_count`,`r`.`status` AS `status`,`u`.`username` AS `created_by_name`,`r`.`created_time` AS `created_time`,`r`.`updated_time` AS `updated_time` from ((`resource` `r` left join `resource_category` `rc` on((`r`.`category_id` = `rc`.`id`))) left join `user` `u` on((`r`.`created_by` = `u`.`id`))) where (`r`.`deleted` = 0);

-- ----------------------------
-- View structure for v_user_favorites
-- ----------------------------
DROP VIEW IF EXISTS `v_user_favorites`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_user_favorites` AS select `uf`.`id` AS `id`,`uf`.`user_id` AS `user_id`,`u`.`username` AS `username`,`uf`.`resource_id` AS `resource_id`,`r`.`name` AS `resource_name`,`r`.`url` AS `resource_url`,`uf`.`created_time` AS `created_time` from ((`user_favorite` `uf` left join `user` `u` on((`uf`.`user_id` = `u`.`id`))) left join `resource` `r` on((`uf`.`resource_id` = `r`.`id`))) where (`r`.`deleted` = 0);

-- ----------------------------
-- Procedure structure for get_user_favorites
-- ----------------------------
DROP PROCEDURE IF EXISTS `get_user_favorites`;
delimiter ;;
CREATE PROCEDURE `get_user_favorites`(IN user_id BIGINT, IN page_num INT, IN page_size INT)
BEGIN
    DECLARE offset_val INT;
    SET offset_val = (page_num - 1) * page_size;
    
    SELECT 
        r.id,
        r.name,
        r.description,
        r.url,
        r.icon,
        rc.name as category_name,
        uf.created_time as favorite_time
    FROM user_favorite uf
    LEFT JOIN resource r ON uf.resource_id = r.id
    LEFT JOIN resource_category rc ON r.category_id = rc.id
    WHERE uf.user_id = user_id AND r.deleted = 0
    ORDER BY uf.created_time DESC
    LIMIT page_size OFFSET offset_val;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for increment_resource_like_count
-- ----------------------------
DROP PROCEDURE IF EXISTS `increment_resource_like_count`;
delimiter ;;
CREATE PROCEDURE `increment_resource_like_count`(IN resource_id BIGINT)
BEGIN
    UPDATE resource 
    SET like_count = like_count + 1, 
        updated_time = CURRENT_TIMESTAMP 
    WHERE id = resource_id AND deleted = 0;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for increment_resource_view_count
-- ----------------------------
DROP PROCEDURE IF EXISTS `increment_resource_view_count`;
delimiter ;;
CREATE PROCEDURE `increment_resource_view_count`(IN resource_id BIGINT)
BEGIN
    UPDATE resource 
    SET view_count = view_count + 1, 
        updated_time = CURRENT_TIMESTAMP 
    WHERE id = resource_id AND deleted = 0;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for record_user_visit
-- ----------------------------
DROP PROCEDURE IF EXISTS `record_user_visit`;
delimiter ;;
CREATE PROCEDURE `record_user_visit`(IN user_id BIGINT, IN resource_id BIGINT)
BEGIN
    INSERT INTO user_history (user_id, resource_id, visit_time)
    VALUES (user_id, resource_id, CURRENT_TIMESTAMP)
    ON DUPLICATE KEY UPDATE visit_time = CURRENT_TIMESTAMP;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table resource
-- ----------------------------
DROP TRIGGER IF EXISTS `before_resource_delete`;
delimiter ;;
CREATE TRIGGER `before_resource_delete` BEFORE UPDATE ON `resource` FOR EACH ROW BEGIN
    IF NEW.deleted = 1 AND OLD.deleted = 0 THEN
        DELETE FROM user_favorite WHERE resource_id = NEW.id;
        DELETE FROM user_history WHERE resource_id = NEW.id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `before_user_delete`;
delimiter ;;
CREATE TRIGGER `before_user_delete` BEFORE UPDATE ON `user` FOR EACH ROW BEGIN
    IF NEW.deleted = 1 AND OLD.deleted = 0 THEN
        DELETE FROM user_favorite WHERE user_id = NEW.id;
        DELETE FROM user_history WHERE user_id = NEW.id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
