@echo off
chcp 65001
echo ========================================
echo ACG资源管理平台启动脚本
echo ========================================
echo.

echo 正在检查环境...
echo.

REM 检查Java环境
java -version
echo 检查到Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java环境，请先安装Java 17或更高版本
    pause
    exit /b 1
)

REM 检查Node.js环境
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js 16或更高版本
    pause
    exit /b 1
)

REM 检查pnpm环境
pnpm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到pnpm环境，请先安装pnpm
    echo 安装命令: npm install -g pnpm
    pause
    exit /b 1
)

REM 检查MySQL服务
echo 正在检查MySQL服务...
sc query mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未检测到MySQL服务，请确保MySQL已启动
    echo.
)

REM 检查Redis服务
echo 正在检查Redis服务...
sc query redis >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未检测到Redis服务，请确保Redis已启动
    echo.
)

echo 环境检查完成！
echo.

echo 请选择启动方式：
echo 1. 启动后端服务
echo 2. 启动前端服务
echo 3. 启动完整项目（后端+前端）
echo 4. 安装依赖
echo 5. 退出
echo.

set /p choice=请输入选择 (1-5): 

if "%choice%"=="1" goto start_backend
if "%choice%"=="2" goto start_frontend
if "%choice%"=="3" goto start_full
if "%choice%"=="4" goto install_deps
if "%choice%"=="5" goto exit
goto invalid_choice

:start_backend
echo.
echo 正在启动后端服务...
cd backend
if not exist "target" (
    echo 正在编译后端项目...
    mvn clean compile
)
echo 启动SpringBoot应用...
mvn spring-boot:run
goto end

:start_frontend
echo.
echo 正在启动前端服务...
cd frontend
if not exist "node_modules" (
    echo 正在安装前端依赖...
    pnpm install
)
echo 启动Vue开发服务器...
pnpm run dev
goto end

:start_full
echo.
echo 正在启动完整项目...
echo 请打开两个命令行窗口分别运行：
echo.
echo 窗口1 - 后端服务：
echo cd backend
echo mvn spring-boot:run
echo.
echo 窗口2 - 前端服务：
echo cd frontend
echo pnpm run dev
echo.
pause
goto end

:install_deps
echo.
echo 正在安装项目依赖...
echo.

echo 安装后端依赖...
cd backend
mvn clean install
cd ..

echo 安装前端依赖...
cd frontend
pnpm install
cd ..

echo 依赖安装完成！
pause
goto end

:invalid_choice
echo.
echo [错误] 无效的选择，请重新运行脚本
pause
goto end

:end
echo.
echo 脚本执行完成！
pause 