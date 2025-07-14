# ACG资源管理平台数据库设计

## 数据库概述

数据库名称：`acg_resource`
字符集：`utf8mb4`
排序规则：`utf8mb4_unicode_ci`

## 表结构设计

### 1. 用户表 (user)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| username | varchar | 50 | NO | - | 用户名，唯一 |
| email | varchar | 100 | NO | - | 邮箱，唯一 |
| password | varchar | 255 | NO | - | 密码（加密） |
| nickname | varchar | 50 | YES | - | 昵称 |
| avatar | varchar | 255 | YES | - | 头像URL |
| role | varchar | 20 | NO | 'USER' | 角色：USER/ADMIN |
| status | tinyint | - | NO | 1 | 状态：0-禁用，1-正常 |
| last_login_time | datetime | - | YES | - | 最后登录时间 |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | datetime | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | - | NO | 0 | 逻辑删除：0-未删除，1-已删除 |

### 2. 资源分类表 (resource_category)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| name | varchar | 50 | NO | - | 分类名称 |
| code | varchar | 50 | NO | - | 分类代码，唯一 |
| description | varchar | 255 | YES | - | 分类描述 |
| icon | varchar | 100 | YES | - | 分类图标 |
| sort_order | int | - | NO | 0 | 排序权重 |
| parent_id | bigint | - | YES | 0 | 父分类ID，0为顶级 |
| status | tinyint | - | NO | 1 | 状态：0-禁用，1-正常 |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | datetime | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | - | NO | 0 | 逻辑删除 |

### 3. 资源表 (resource)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| name | varchar | 100 | NO | - | 资源名称 |
| description | text | - | YES | - | 资源描述 |
| url | varchar | 500 | NO | - | 资源链接 |
| icon | varchar | 255 | YES | - | 资源图标URL |
| category_id | bigint | - | NO | - | 分类ID |
| tags | varchar | 255 | YES | - | 标签，逗号分隔 |
| view_count | bigint | - | NO | 0 | 访问次数 |
| like_count | bigint | - | NO | 0 | 点赞次数 |
| status | tinyint | - | NO | 1 | 状态：0-禁用，1-正常 |
| created_by | bigint | - | NO | - | 创建者ID |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | datetime | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | - | NO | 0 | 逻辑删除 |

### 4. 用户收藏表 (user_favorite)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| user_id | bigint | - | NO | - | 用户ID |
| resource_id | bigint | - | NO | - | 资源ID |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 收藏时间 |

### 5. 用户浏览历史表 (user_history)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| user_id | bigint | - | NO | - | 用户ID |
| resource_id | bigint | - | NO | - | 资源ID |
| visit_time | datetime | - | NO | CURRENT_TIMESTAMP | 访问时间 |

### 6. 系统配置表 (system_config)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| config_key | varchar | 100 | NO | - | 配置键，唯一 |
| config_value | text | - | YES | - | 配置值 |
| description | varchar | 255 | YES | - | 配置描述 |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | datetime | - | NO | CURRENT_TIMESTAMP | 更新时间 |

### 7. 操作日志表 (operation_log)

| 字段名 | 类型 | 长度 | 是否为空 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | NO | - | 主键，自增 |
| user_id | bigint | - | YES | - | 操作用户ID |
| operation | varchar | 100 | NO | - | 操作类型 |
| module | varchar | 50 | NO | - | 操作模块 |
| description | varchar | 255 | YES | - | 操作描述 |
| ip_address | varchar | 50 | YES | - | IP地址 |
| user_agent | varchar | 500 | YES | - | 用户代理 |
| created_time | datetime | - | NO | CURRENT_TIMESTAMP | 操作时间 |

## 索引设计

### 用户表索引
- `idx_username` (username) - 用户名唯一索引
- `idx_email` (email) - 邮箱唯一索引
- `idx_status` (status) - 状态索引

### 资源分类表索引
- `idx_code` (code) - 分类代码唯一索引
- `idx_parent_id` (parent_id) - 父分类索引
- `idx_sort_order` (sort_order) - 排序索引

### 资源表索引
- `idx_category_id` (category_id) - 分类索引
- `idx_status` (status) - 状态索引
- `idx_view_count` (view_count) - 访问量索引
- `idx_created_time` (created_time) - 创建时间索引

### 用户收藏表索引
- `idx_user_resource` (user_id, resource_id) - 用户资源唯一索引
- `idx_user_id` (user_id) - 用户索引

### 用户历史表索引
- `idx_user_id` (user_id) - 用户索引
- `idx_visit_time` (visit_time) - 访问时间索引

## 初始数据

### 默认分类数据
```sql
INSERT INTO resource_category (name, code, description, icon, sort_order, parent_id) VALUES
('动漫专区', 'anime', '动漫相关资源', 'anime-icon', 1, 0),
('在线动漫', 'anime-online', '在线观看动漫', 'play-icon', 1, 1),
('动漫下载', 'anime-download', '动漫下载资源', 'download-icon', 2, 1),
('影音专区', 'video', '影视音乐资源', 'video-icon', 2, 0),
('娱乐导航', 'entertainment', '娱乐相关资源', 'entertainment-icon', 3, 0),
('实用软件', 'software', '实用软件工具', 'software-icon', 4, 0),
('次元美图', 'images', '二次元图片资源', 'image-icon', 5, 0),
('在线漫画', 'manga', '在线漫画资源', 'manga-icon', 6, 0),
('ACG游戏', 'game', 'ACG游戏资源', 'game-icon', 7, 0),
('ACG社区', 'community', 'ACG社区论坛', 'community-icon', 8, 0),
('ASMR资源', 'asmr', 'ASMR音频资源', 'audio-icon', 9, 0),
('网盘资源', 'cloud', '网盘磁力资源', 'cloud-icon', 10, 0);
```

### 默认管理员用户
```sql
INSERT INTO user (username, email, password, nickname, role) VALUES
('admin', 'admin@acg.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN');
-- 密码为: admin123
```

## 数据库创建脚本

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS acg_resource 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE acg_resource;

-- 创建用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status TINYINT NOT NULL DEFAULT 1,
    last_login_time DATETIME,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

-- 创建资源分类表
CREATE TABLE resource_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    parent_id BIGINT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
);

-- 创建资源表
CREATE TABLE resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    url VARCHAR(500) NOT NULL,
    icon VARCHAR(255),
    category_id BIGINT NOT NULL,
    tags VARCHAR(255),
    view_count BIGINT NOT NULL DEFAULT 0,
    like_count BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_by BIGINT NOT NULL,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_view_count (view_count),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (category_id) REFERENCES resource_category(id),
    FOREIGN KEY (created_by) REFERENCES user(id)
);

-- 创建用户收藏表
CREATE TABLE user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_user_resource (user_id, resource_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);

-- 创建用户浏览历史表
CREATE TABLE user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    visit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_visit_time (visit_time),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);

-- 创建系统配置表
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(255),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建操作日志表
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    operation VARCHAR(100) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (user_id) REFERENCES user(id)
);
``` 