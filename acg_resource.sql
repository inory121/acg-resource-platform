-- =====================================================
-- ACG资源管理平台数据库初始化脚本
-- 数据库名称: acg_resource
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS acg_resource 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE acg_resource;

-- =====================================================
-- 1. 用户表 (user)
-- =====================================================
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 资源分类表 (resource_category)
-- =====================================================
CREATE TABLE resource_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类代码',
    description VARCHAR(255) COMMENT '分类描述',
    icon VARCHAR(100) COMMENT '分类图标',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分类表';

-- =====================================================
-- 3. 资源表 (resource)
-- =====================================================
CREATE TABLE resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '资源名称',
    description TEXT COMMENT '资源描述',
    url VARCHAR(500) NOT NULL COMMENT '资源链接',
    icon VARCHAR(255) COMMENT '资源图标URL',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    view_count BIGINT NOT NULL DEFAULT 0 COMMENT '访问次数',
    like_count BIGINT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_view_count (view_count),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (category_id) REFERENCES resource_category(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- =====================================================
-- 4. 用户收藏表 (user_favorite)
-- =====================================================
CREATE TABLE user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY idx_user_resource (user_id, resource_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES resource(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- =====================================================
-- 5. 用户浏览历史表 (user_history)
-- =====================================================
CREATE TABLE user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    visit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    INDEX idx_user_id (user_id),
    INDEX idx_visit_time (visit_time),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES resource(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户浏览历史表';

-- =====================================================
-- 6. 系统配置表 (system_config)
-- =====================================================
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置描述',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 7. 操作日志表 (operation_log)
-- =====================================================
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '操作用户ID',
    operation VARCHAR(100) NOT NULL COMMENT '操作类型',
    module VARCHAR(50) NOT NULL COMMENT '操作模块',
    description VARCHAR(255) COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 初始数据
-- =====================================================

-- 插入默认管理员用户
-- 密码: admin123 (BCrypt加密)
INSERT INTO user (username, email, password, nickname, role, status) VALUES
('admin', 'admin@acg.com', '$2a$10$nIX1C54RaQderWSFEdTWreE7D1RLREjL.7cC8EZ5C7jGH2YKbH1Ty', '管理员', 'ADMIN', 1);

-- 插入默认分类数据
INSERT INTO resource_category (name, code, description, icon, sort_order, parent_id, status) VALUES
('动漫专区', 'anime', '动漫相关资源', 'fas fa-tv', 1, 0, 1),
('在线动漫', 'anime-online', '在线观看动漫', 'fas fa-play', 1, 1, 1),
('动漫下载', 'anime-download', '动漫下载资源', 'fas fa-download', 2, 1, 1),
('影音专区', 'video', '影视音乐资源', 'fas fa-video', 2, 0, 1),
('娱乐导航', 'entertainment', '娱乐相关资源', 'fas fa-music', 3, 0, 1),
('实用软件', 'software', '实用软件工具', 'fas fa-tools', 4, 0, 1),
('次元美图', 'images', '二次元图片资源', 'fas fa-images', 5, 0, 1),
('在线漫画', 'manga', '在线漫画资源', 'fas fa-book', 6, 0, 1),
('ACG游戏', 'game', 'ACG游戏资源', 'fas fa-gamepad', 7, 0, 1),
('ACG社区', 'community', 'ACG社区论坛', 'fas fa-users', 8, 0, 1),
('ASMR资源', 'asmr', 'ASMR音频资源', 'fas fa-headphones', 9, 0, 1),
('网盘资源', 'cloud', '网盘磁力资源', 'fas fa-cloud', 10, 0, 1);

-- 插入示例资源数据
INSERT INTO resource (name, description, url, icon, category_id, tags, view_count, like_count, status, created_by) VALUES
('哔哩哔哩', '中国领先的年轻人文化社区和视频平台', 'https://www.bilibili.com', 'fas fa-play-circle', 2, '视频,动漫,娱乐', 1000, 500, 1, 1),
('樱花动漫', '免费在线观看动漫的网站', 'https://www.yhdm.tv', 'fas fa-tv', 2, '动漫,在线观看', 800, 300, 1, 1),
('动漫花园', '动漫资源下载网站', 'https://share.dmhy.org', 'fas fa-download', 3, '动漫,下载,BT', 600, 200, 1, 1),
('爱奇艺', '中国高品质视频娱乐服务提供商', 'https://www.iqiyi.com', 'fas fa-play', 4, '视频,影视,娱乐', 1200, 600, 1, 1),
('网易云音乐', '专注于发现与分享的音乐平台', 'https://music.163.com', 'fas fa-music', 5, '音乐,娱乐,在线', 900, 400, 1, 1),
('GitHub', '全球最大的代码托管平台', 'https://github.com', 'fas fa-code', 6, '编程,开源,工具', 1500, 800, 1, 1),
('Pixiv', '日本插画社交网站', 'https://www.pixiv.net', 'fas fa-palette', 7, '插画,二次元,艺术', 700, 350, 1, 1),
('漫画DB', '在线漫画阅读网站', 'https://www.manhuadb.com', 'fas fa-book-open', 8, '漫画,在线阅读', 500, 250, 1, 1),
('Steam', '全球最大的数字游戏发行平台', 'https://store.steampowered.com', 'fas fa-gamepad', 9, '游戏,数字发行', 1100, 550, 1, 1),
('NGA论坛', '艾泽拉斯国家地理论坛', 'https://bbs.nga.cn', 'fas fa-comments', 10, '论坛,游戏,社区', 400, 150, 1, 1);

-- 插入系统配置数据
INSERT INTO system_config (config_key, config_value, description) VALUES
('site_name', 'ACG资源管理平台', '网站名称'),
('site_description', '一个专注于ACG资源分享和管理的平台', '网站描述'),
('site_keywords', 'ACG,动漫,资源,分享,管理', '网站关键词'),
('site_logo', '/logo.png', '网站Logo'),
('site_favicon', '/favicon.ico', '网站图标'),
('max_upload_size', '10485760', '最大上传文件大小（字节）'),
('allowed_file_types', 'jpg,jpeg,png,gif,mp4,avi,mov,zip,rar', '允许上传的文件类型'),
('enable_registration', 'true', '是否允许用户注册'),
('enable_comment', 'true', '是否允许评论'),
('enable_rating', 'true', '是否允许评分');

-- =====================================================
-- 创建视图
-- =====================================================

-- 资源统计视图
CREATE VIEW v_resource_stats AS
SELECT 
    r.id,
    r.name,
    r.description,
    r.url,
    r.icon,
    r.category_id,
    rc.name as category_name,
    r.tags,
    r.view_count,
    r.like_count,
    r.status,
    u.username as created_by_name,
    r.created_time,
    r.updated_time
FROM resource r
LEFT JOIN resource_category rc ON r.category_id = rc.id
LEFT JOIN user u ON r.created_by = u.id
WHERE r.deleted = 0;

-- 用户收藏统计视图
CREATE VIEW v_user_favorites AS
SELECT 
    uf.id,
    uf.user_id,
    u.username,
    uf.resource_id,
    r.name as resource_name,
    r.url as resource_url,
    uf.created_time
FROM user_favorite uf
LEFT JOIN user u ON uf.user_id = u.id
LEFT JOIN resource r ON uf.resource_id = r.id
WHERE r.deleted = 0;

-- 热门资源视图
CREATE VIEW v_hot_resources AS
SELECT 
    r.id,
    r.name,
    r.description,
    r.url,
    r.icon,
    r.category_id,
    rc.name as category_name,
    r.view_count,
    r.like_count,
    r.created_time
FROM resource r
LEFT JOIN resource_category rc ON r.category_id = rc.id
WHERE r.deleted = 0 AND r.status = 1
ORDER BY r.view_count DESC, r.like_count DESC;

-- =====================================================
-- 创建存储过程
-- =====================================================

DELIMITER //

-- 增加资源访问次数
CREATE PROCEDURE increment_resource_view_count(IN resource_id BIGINT)
BEGIN
    UPDATE resource 
    SET view_count = view_count + 1, 
        updated_time = CURRENT_TIMESTAMP 
    WHERE id = resource_id AND deleted = 0;
END //

-- 增加资源点赞次数
CREATE PROCEDURE increment_resource_like_count(IN resource_id BIGINT)
BEGIN
    UPDATE resource 
    SET like_count = like_count + 1, 
        updated_time = CURRENT_TIMESTAMP 
    WHERE id = resource_id AND deleted = 0;
END //

-- 记录用户访问历史
CREATE PROCEDURE record_user_visit(IN user_id BIGINT, IN resource_id BIGINT)
BEGIN
    INSERT INTO user_history (user_id, resource_id, visit_time)
    VALUES (user_id, resource_id, CURRENT_TIMESTAMP)
    ON DUPLICATE KEY UPDATE visit_time = CURRENT_TIMESTAMP;
END //

-- 获取用户收藏列表
CREATE PROCEDURE get_user_favorites(IN user_id BIGINT, IN page_num INT, IN page_size INT)
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
END //

DELIMITER ;

-- =====================================================
-- 创建触发器
-- =====================================================

-- 删除资源时同时删除相关收藏和历史记录
DELIMITER //
CREATE TRIGGER before_resource_delete
BEFORE UPDATE ON resource
FOR EACH ROW
BEGIN
    IF NEW.deleted = 1 AND OLD.deleted = 0 THEN
        DELETE FROM user_favorite WHERE resource_id = NEW.id;
        DELETE FROM user_history WHERE resource_id = NEW.id;
    END IF;
END //
DELIMITER ;

-- 删除用户时同时删除相关收藏和历史记录
DELIMITER //
CREATE TRIGGER before_user_delete
BEFORE UPDATE ON user
FOR EACH ROW
BEGIN
    IF NEW.deleted = 1 AND OLD.deleted = 0 THEN
        DELETE FROM user_favorite WHERE user_id = NEW.id;
        DELETE FROM user_history WHERE user_id = NEW.id;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- 完成提示
-- =====================================================
SELECT 'ACG资源管理平台数据库初始化完成！' as message;
SELECT '默认管理员账号: admin' as username;
SELECT '默认管理员密码: admin123' as password;
SELECT '请及时修改默认密码！' as warning; 