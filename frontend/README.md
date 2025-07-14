# ACG资源管理平台 - 前端

## 技术栈

- **框架**: Vue 3 + TypeScript
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **构建工具**: Vite
- **包管理器**: pnpm

## 环境要求

- Node.js >= 16.0
- pnpm >= 8.0

## 安装pnpm

如果还没有安装pnpm，请先安装：

```bash
# 使用npm安装pnpm
npm install -g pnpm

# 或使用其他方式安装
# Windows (PowerShell)
iwr https://get.pnpm.io/install.ps1 -useb | iex

# macOS/Linux
curl -fsSL https://get.pnpm.io/install.sh | sh -
```

## 开发指南

### 安装依赖

```bash
pnpm install
```

### 启动开发服务器

```bash
pnpm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
pnpm run build
```

### 预览生产构建

```bash
pnpm run preview
```

### 代码检查

```bash
pnpm run lint
```

## 项目结构

```
frontend/
├── src/
│   ├── views/          # 页面组件
│   ├── router/         # 路由配置
│   ├── stores/         # Pinia状态管理
│   ├── api/            # API接口
│   ├── utils/          # 工具函数
│   ├── components/     # 公共组件
│   ├── assets/         # 静态资源
│   ├── App.vue         # 根组件
│   ├── main.ts         # 入口文件
│   └── style.css       # 全局样式
├── public/             # 公共资源
├── package.json        # 依赖配置
├── vite.config.ts      # Vite配置
├── tsconfig.json       # TypeScript配置
├── .npmrc             # pnpm配置
└── pnpm-workspace.yaml # 工作区配置
```

## pnpm优势

相比npm，pnpm具有以下优势：

1. **更快的安装速度**: 使用硬链接和符号链接，避免重复下载
2. **更少的磁盘空间**: 全局存储，避免重复安装相同依赖
3. **更严格的依赖管理**: 防止幽灵依赖，确保依赖关系正确
4. **更好的monorepo支持**: 内置工作区功能

## 常用命令

```bash
# 安装依赖
pnpm install

# 添加依赖
pnpm add <package-name>

# 添加开发依赖
pnpm add -D <package-name>

# 移除依赖
pnpm remove <package-name>

# 更新依赖
pnpm update

# 运行脚本
pnpm run <script-name>

# 清理缓存
pnpm store prune
```

## 开发注意事项

1. **依赖安装**: 使用 `pnpm install` 而不是 `npm install`
2. **锁文件**: 项目使用 `pnpm-lock.yaml` 作为锁文件
3. **缓存**: pnpm缓存存储在 `.pnpm-store/` 目录
4. **兼容性**: 某些包可能需要 `shamefully-hoist=true` 配置

## 故障排除

### 依赖安装失败

```bash
# 清理缓存并重新安装
pnpm store prune
rm -rf node_modules pnpm-lock.yaml
pnpm install
```

### 构建失败

```bash
# 检查Node.js版本
node --version

# 清理构建缓存
rm -rf dist
pnpm run build
```

### 端口冲突

如果3000端口被占用，可以修改 `vite.config.ts` 中的端口配置：

```typescript
export default defineConfig({
  server: {
    port: 3001 // 修改端口
  }
})
```

## 部署

### 构建生产版本

```bash
pnpm run build
```

构建产物在 `dist/` 目录中。

### Docker部署

```dockerfile
FROM node:16-alpine as build

WORKDIR /app

# 安装pnpm
RUN npm install -g pnpm

COPY package*.json pnpm-lock.yaml ./
RUN pnpm install

COPY . .
RUN pnpm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证。 