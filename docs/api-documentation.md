# ACG资源管理平台API文档

## 概述

本文档描述了ACG资源管理平台的所有API接口。API采用RESTful设计风格，使用JSON格式进行数据交换。

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token (Bearer Token)
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

## 认证相关

### 1. 用户注册

**接口**: `POST /auth/register`

**请求参数**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "nickname": "测试用户"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "role": "USER",
    "status": 1,
    "createdTime": "2024-01-01T12:00:00"
  }
}
```

### 2. 用户登录

**接口**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg",
      "role": "USER"
    }
  }
}
```

### 3. 刷新Token

**接口**: `POST /auth/refresh`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

## 用户管理

### 1. 获取用户信息

**接口**: `GET /user/profile`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "role": "USER",
    "status": 1,
    "lastLoginTime": "2024-01-01T12:00:00",
    "createdTime": "2024-01-01T10:00:00"
  }
}
```

### 2. 更新用户信息

**接口**: `PUT /user/profile`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/new-avatar.jpg"
}
```

### 3. 修改密码

**接口**: `PUT /user/password`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

## 资源分类管理

### 1. 获取分类列表

**接口**: `GET /categories`

**查询参数**:
- `parentId`: 父分类ID（可选）
- `status`: 状态（可选，1-正常，0-禁用）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "动漫专区",
      "code": "anime",
      "description": "动漫相关资源",
      "icon": "anime-icon",
      "sortOrder": 1,
      "parentId": 0,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "在线动漫",
          "code": "anime-online",
          "description": "在线观看动漫",
          "icon": "play-icon",
          "sortOrder": 1,
          "parentId": 1,
          "status": 1
        }
      ]
    }
  ]
}
```

### 2. 获取分类详情

**接口**: `GET /categories/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "动漫专区",
    "code": "anime",
    "description": "动漫相关资源",
    "icon": "anime-icon",
    "sortOrder": 1,
    "parentId": 0,
    "status": 1,
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

### 3. 创建分类（管理员）

**接口**: `POST /admin/categories`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "name": "新分类",
  "code": "new-category",
  "description": "新分类描述",
  "icon": "new-icon",
  "sortOrder": 10,
  "parentId": 0
}
```

### 4. 更新分类（管理员）

**接口**: `PUT /admin/categories/{id}`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "name": "更新后的分类名",
  "description": "更新后的描述",
  "icon": "updated-icon",
  "sortOrder": 5
}
```

### 5. 删除分类（管理员）

**接口**: `DELETE /admin/categories/{id}`

**请求头**: `Authorization: Bearer {token}`

## 资源管理

### 1. 获取资源列表

**接口**: `GET /resources`

**查询参数**:
- `categoryId`: 分类ID（可选）
- `keyword`: 搜索关键词（可选）
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）
- `sortBy`: 排序字段（可选：viewCount, likeCount, createdTime）
- `sortOrder`: 排序方向（可选：asc, desc）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "哔哩哔哩",
        "description": "中国领先的年轻人文化社区",
        "url": "https://www.bilibili.com",
        "icon": "https://example.com/bilibili.png",
        "categoryId": 1,
        "categoryName": "动漫专区",
        "tags": "视频,动漫,弹幕",
        "viewCount": 10000,
        "likeCount": 500,
        "status": 1,
        "createdBy": 1,
        "createdTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 20,
    "current": 1,
    "pages": 5
  }
}
```

### 2. 获取资源详情

**接口**: `GET /resources/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "哔哩哔哩",
    "description": "中国领先的年轻人文化社区",
    "url": "https://www.bilibili.com",
    "icon": "https://example.com/bilibili.png",
    "categoryId": 1,
    "categoryName": "动漫专区",
    "tags": "视频,动漫,弹幕",
    "viewCount": 10000,
    "likeCount": 500,
    "status": 1,
    "createdBy": 1,
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

### 3. 创建资源（管理员）

**接口**: `POST /admin/resources`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "name": "新资源",
  "description": "资源描述",
  "url": "https://example.com",
  "icon": "https://example.com/icon.png",
  "categoryId": 1,
  "tags": "标签1,标签2"
}
```

### 4. 更新资源（管理员）

**接口**: `PUT /admin/resources/{id}`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "name": "更新后的资源名",
  "description": "更新后的描述",
  "url": "https://updated-example.com",
  "icon": "https://example.com/updated-icon.png",
  "categoryId": 2,
  "tags": "新标签1,新标签2"
}
```

### 5. 删除资源（管理员）

**接口**: `DELETE /admin/resources/{id}`

**请求头**: `Authorization: Bearer {token}`

### 6. 点赞资源

**接口**: `POST /resources/{id}/like`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "likeCount": 501
  }
}
```

### 7. 取消点赞

**接口**: `DELETE /resources/{id}/like`

**请求头**: `Authorization: Bearer {token}`

## 用户收藏

### 1. 获取用户收藏列表

**接口**: `GET /user/favorites`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "resourceId": 1,
        "resourceName": "哔哩哔哩",
        "resourceDescription": "中国领先的年轻人文化社区",
        "resourceUrl": "https://www.bilibili.com",
        "resourceIcon": "https://example.com/bilibili.png",
        "createdTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 10,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 添加收藏

**接口**: `POST /user/favorites`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "resourceId": 1
}
```

### 3. 取消收藏

**接口**: `DELETE /user/favorites/{resourceId}`

**请求头**: `Authorization: Bearer {token}`

## 浏览历史

### 1. 获取浏览历史

**接口**: `GET /user/history`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "resourceId": 1,
        "resourceName": "哔哩哔哩",
        "resourceDescription": "中国领先的年轻人文化社区",
        "resourceUrl": "https://www.bilibili.com",
        "resourceIcon": "https://example.com/bilibili.png",
        "visitTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 50,
    "size": 20,
    "current": 1,
    "pages": 3
  }
}
```

### 2. 清空浏览历史

**接口**: `DELETE /user/history`

**请求头**: `Authorization: Bearer {token}`

## 搜索功能

### 1. 搜索资源

**接口**: `GET /search`

**查询参数**:
- `q`: 搜索关键词
- `categoryId`: 分类ID（可选）
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "哔哩哔哩",
        "description": "中国领先的年轻人文化社区",
        "url": "https://www.bilibili.com",
        "icon": "https://example.com/bilibili.png",
        "categoryId": 1,
        "categoryName": "动漫专区",
        "tags": "视频,动漫,弹幕",
        "viewCount": 10000,
        "likeCount": 500,
        "status": 1
      }
    ],
    "total": 5,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

## 统计功能

### 1. 获取首页统计数据

**接口**: `GET /stats/home`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalResources": 1000,
    "totalCategories": 20,
    "totalUsers": 500,
    "todayVisits": 1500,
    "hotResources": [
      {
        "id": 1,
        "name": "哔哩哔哩",
        "viewCount": 10000,
        "likeCount": 500
      }
    ],
    "newResources": [
      {
        "id": 2,
        "name": "新资源",
        "createdTime": "2024-01-01T12:00:00"
      }
    ]
  }
}
```

### 2. 获取管理后台统计数据

**接口**: `GET /admin/stats`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userStats": {
      "totalUsers": 500,
      "activeUsers": 300,
      "newUsersToday": 10
    },
    "resourceStats": {
      "totalResources": 1000,
      "activeResources": 950,
      "newResourcesToday": 5
    },
    "visitStats": {
      "todayVisits": 1500,
      "weekVisits": 10000,
      "monthVisits": 50000
    }
  }
}
```

## 文件上传

### 1. 上传图片

**接口**: `POST /upload/image`

**请求头**: `Authorization: Bearer {token}`

**请求体**: `multipart/form-data`

**参数**:
- `file`: 图片文件

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/uploads/image.jpg",
    "filename": "image.jpg",
    "size": 1024000
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 分页参数说明

所有支持分页的接口都使用以下参数：

- `page`: 页码，从1开始
- `size`: 每页大小，默认20，最大100

分页响应格式：
```json
{
  "records": [], // 数据列表
  "total": 100,  // 总记录数
  "size": 20,    // 每页大小
  "current": 1,  // 当前页码
  "pages": 5     // 总页数
}
```

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer {token}`
2. 文件上传接口支持的最大文件大小为10MB
3. 搜索功能支持模糊匹配
4. 时间格式统一使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`
5. 所有接口都支持CORS跨域请求

## 接口测试

可以使用以下工具测试API接口：

1. **Postman**: 推荐使用Postman进行接口测试
2. **curl**: 命令行工具
3. **Swagger UI**: 访问 `http://localhost:8080/api/doc.html` 查看在线API文档

### 测试示例

```bash
# 用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取资源列表
curl -X GET http://localhost:8080/api/resources \
  -H "Authorization: Bearer {token}"
``` 