# ACG资源管理平台部署文档

## 环境要求

### 系统要求
- **操作系统**: Windows 10/11, macOS 10.15+, Ubuntu 18.04+
- **内存**: 最低 4GB RAM，推荐 8GB RAM
- **存储**: 最低 10GB 可用空间
- **网络**: 稳定的互联网连接

### 软件要求

#### 后端环境
- **Java**: JDK 17 或更高版本
- **Maven**: 3.6+ 
- **MySQL**: 8.0 或更高版本
- **Redis**: 6.0 或更高版本

#### 前端环境
- **Node.js**: 16.0 或更高版本
- **pnpm**: 8.0 或更高版本

## 安装步骤

### 1. 环境准备

#### 安装Java
```bash
# 下载并安装JDK 17
# 设置JAVA_HOME环境变量
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

#### 安装Maven
```bash
# 下载Maven
wget https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
tar -xzf apache-maven-3.9.5-bin.tar.gz
sudo mv apache-maven-3.9.5 /opt/maven

# 设置环境变量
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH
```

#### 安装MySQL
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

#### 安装Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# 启动Redis服务
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

#### 安装Node.js和pnpm
```bash
# 使用nvm安装Node.js
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 16
nvm use 16

# 安装pnpm
npm install -g pnpm
```

### 2. 数据库配置

#### 创建数据库
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE acg_resource CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'acg_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON acg_resource.* TO 'acg_user'@'localhost';
FLUSH PRIVILEGES;

-- 执行数据库初始化脚本
USE acg_resource;
SOURCE docs/database-design.md;
```

### 3. 项目配置

#### 后端配置
1. 修改 `backend/src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/acg_resource?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: acg_user
    password: your_password
    
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果设置了密码
```

2. 配置JWT密钥
```yaml
jwt:
  secret: your-jwt-secret-key-here
  expiration: 86400000
```

#### 前端配置
1. 修改API地址（如果需要）
```typescript
// frontend/src/api/config.ts
export const API_BASE_URL = 'http://localhost:8080/api'
```

### 4. 项目构建

#### 后端构建
```bash
cd backend
mvn clean package -DskipTests
```

#### 前端构建
```bash
cd frontend
pnpm install
pnpm run build
```

### 5. 启动服务

#### 开发环境启动
```bash
# 启动后端
cd backend
mvn spring-boot:run

# 启动前端（新终端）
cd frontend
pnpm run dev
```

#### 生产环境启动
```bash
# 启动后端
cd backend
java -jar target/acg-resource-platform-1.0.0.jar

# 部署前端
cd frontend
pnpm run build
# 将dist目录部署到Nginx
```

## Docker部署

### 1. 创建Dockerfile

#### 后端Dockerfile
```dockerfile
# backend/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/acg-resource-platform-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 前端Dockerfile
```dockerfile
# frontend/Dockerfile
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
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### 2. Docker Compose配置
```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: acg_resource
      MYSQL_USER: acg_user
      MYSQL_PASSWORD: user_password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docs/database-design.md:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  backend:
    build: ./backend
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/acg_resource
      SPRING_DATASOURCE_USERNAME: acg_user
      SPRING_DATASOURCE_PASSWORD: user_password
      SPRING_REDIS_HOST: redis
    ports:
      - "8080:8080"

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 3. 启动Docker服务
```bash
docker-compose up -d
```

## Nginx配置

### 生产环境Nginx配置
```nginx
# /etc/nginx/sites-available/acg-resource
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/acg-resource/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 监控和维护

### 1. 日志管理
```bash
# 查看后端日志
tail -f logs/acg-resource.log

# 查看Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 2. 数据库备份
```bash
# 创建备份脚本
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u acg_user -p acg_resource > backup/acg_resource_$DATE.sql
```

### 3. 性能监控
- 使用JVM监控工具（JConsole, VisualVM）
- 配置Redis监控
- 设置MySQL慢查询日志

## 故障排除

### 常见问题

#### 1. 数据库连接失败
```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 检查端口是否开放
netstat -tlnp | grep 3306
```

#### 2. Redis连接失败
```bash
# 检查Redis服务状态
sudo systemctl status redis-server

# 测试Redis连接
redis-cli ping
```

#### 3. 前端构建失败
```bash
# 清理缓存
pnpm store prune
rm -rf node_modules pnpm-lock.yaml
pnpm install
```

#### 4. 后端启动失败
```bash
# 检查Java版本
java -version

# 检查Maven配置
mvn -version

# 查看详细错误日志
mvn spring-boot:run -X
```

## 安全配置

### 1. 防火墙设置
```bash
# Ubuntu/Debian
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable
```

### 2. SSL证书配置
```bash
# 使用Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

### 3. 数据库安全
```sql
-- 限制数据库用户权限
REVOKE ALL PRIVILEGES ON *.* FROM 'acg_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON acg_resource.* TO 'acg_user'@'localhost';
```

## 更新部署

### 1. 代码更新
```bash
# 拉取最新代码
git pull origin main

# 重新构建
cd backend && mvn clean package
cd ../frontend && pnpm run build

# 重启服务
sudo systemctl restart acg-backend
sudo systemctl restart nginx
```

### 2. 数据库迁移
```bash
# 备份当前数据
mysqldump -u acg_user -p acg_resource > backup_before_migration.sql

# 执行迁移脚本
mysql -u acg_user -p acg_resource < migration_script.sql
```

## 联系支持

如果在部署过程中遇到问题，请：

1. 查看项目文档：`docs/`
2. 检查日志文件
3. 提交Issue到项目仓库
4. 联系技术支持团队

---

**注意**: 请根据实际环境调整配置参数，确保安全性和性能。 