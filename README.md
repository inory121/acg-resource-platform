# ACG资源管理平台

## 项目概述

ACG资源管理平台是一个基于SpringBoot+Vue技术栈的网站管理软件，旨在为ACG爱好者提供资源导航、分类管理和快速访问功能。

## 技术架构

### 前端技术栈
- **框架**: Vue 3 + TypeScript
- **UI组件库**: Element Plus（支持亮色/暗色主题）
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **图标库**: Font Awesome

### 后端技术栈
- **核心框架**: SpringBoot 最新版本
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **安全框架**: Spring Security + JWT
- **API文档**: Swagger/Knife4j

## 功能特性

### 用户模块
- 用户注册/登录（邮箱/用户名）
- 用户权限管理（普通用户/管理员）
- 用户收藏功能
- 浏览历史记录

### 资源管理模块
- 动漫专区（在线动漫、动漫下载）
- 影音专区
- 娱乐导航
- 实用软件/工具
- 次元美图（pixiv、Danbooru等）
- 在线漫画/日轻小说
- ACG游戏（Galgame资源）
- ACG社区
- ASMR资源
- 网盘/磁力资源

### 导航管理模块
- 左侧垂直导航栏（可折叠）
- 顶部横向导航栏
- 面包屑导航
- 相关导航推荐

### 管理后台
- 资源管理（CRUD操作）
- 分类管理
- 用户管理
- 数据统计
- 系统设置

## 快速开始

### 环境要求
- Node.js >= 16
- Java >= 17
- MySQL >= 8.0
- Redis >= 6.0

### 后端启动
```bash
cd backend
# 配置数据库连接
# 启动Redis服务
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
pnpm install
pnpm run dev
```

## 项目结构

```
acg-resource-platform/
├── backend/          # 后端SpringBoot项目
├── frontend/         # 前端Vue3项目
└── docs/            # 项目文档
```

## 开发计划

- **第一阶段**: 基础框架搭建、数据库设计
- **第二阶段**: 用户模块、资源分类管理
- **第三阶段**: 资源展示、搜索功能、管理后台
- **第四阶段**: 性能优化、安全加固、部署上线

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进这个项目。

## 许可证

MIT License 