# 管理员功能测试指南

## 测试环境准备

1. 确保后端服务正常运行
2. 确保前端服务正常运行
3. 确保数据库已初始化并包含默认管理员账号

## 测试步骤

### 1. 管理员登录测试

1. 访问前端登录页面
2. 使用默认管理员账号登录：
   - 用户名：`admin`
   - 密码：`admin123`
3. 验证登录成功后可以看到用户头像
4. 点击用户头像，验证下拉菜单中包含"管理后台"选项

### 2. 管理后台访问测试

1. 点击"管理后台"进入管理界面
2. 验证左侧导航栏包含：
   - 仪表盘
   - 资源管理
   - 分类管理
   - 用户管理

### 3. 仪表盘功能测试

1. 进入仪表盘页面
2. 验证统计数据正常显示：
   - 总用户数
   - 总资源数
   - 总分类数
   - 总浏览量
3. 验证最近活动列表正常显示

### 4. 用户管理功能测试

1. 进入用户管理页面
2. 验证用户列表正常显示
3. 测试搜索功能：
   - 输入用户名搜索
   - 选择角色筛选
   - 选择状态筛选
4. 测试用户操作：
   - 启用/禁用用户
   - 设置/取消管理员权限
   - 删除用户

### 5. 资源管理功能测试

1. 进入资源管理页面
2. 验证资源列表正常显示
3. 测试搜索和筛选功能
4. 测试资源操作：
   - 添加新资源
   - 编辑资源信息
   - 删除资源

### 6. 分类管理功能测试

1. 进入分类管理页面
2. 验证分类列表正常显示
3. 测试分类操作：
   - 添加新分类
   - 编辑分类信息
   - 删除分类

### 7. 权限控制测试

1. 使用普通用户账号登录
2. 尝试直接访问管理后台URL：`/admin`
3. 验证是否被重定向到首页并显示权限提示

### 8. API接口测试

使用Postman或类似工具测试以下API：

#### 管理员登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 获取用户列表
```http
GET /api/admin/users?current=1&size=10
Authorization: Bearer {token}
```

#### 获取仪表盘统计
```http
GET /api/admin/dashboard/stats
Authorization: Bearer {token}
```

## 预期结果

### 成功场景
- 管理员可以正常登录
- 可以访问所有管理功能
- API接口返回正确的数据
- 权限控制正常工作

### 失败场景
- 普通用户无法访问管理后台
- 无效token无法访问管理员API
- 错误操作会显示相应的错误提示

## 常见问题排查

### 1. 登录失败
- 检查数据库连接
- 验证默认管理员账号是否存在
- 检查密码是否正确

### 2. 管理后台无法访问
- 检查用户角色是否正确
- 验证localStorage中的token和role
- 检查路由守卫配置

### 3. API调用失败
- 检查后端服务是否正常运行
- 验证API路径是否正确
- 检查请求头中的token格式

### 4. 数据不显示
- 检查数据库连接
- 验证API响应格式
- 查看浏览器控制台错误信息

## 性能测试

### 1. 并发测试
- 模拟多个管理员同时操作
- 测试大量数据的分页性能

### 2. 响应时间测试
- 测试页面加载时间
- 测试API响应时间

## 安全测试

### 1. 权限绕过测试
- 尝试直接访问管理员API
- 测试无效token的处理

### 2. 数据验证测试
- 测试输入验证
- 测试SQL注入防护

## 测试报告

完成测试后，请记录以下信息：
- 测试环境
- 测试结果
- 发现的问题
- 建议的改进 