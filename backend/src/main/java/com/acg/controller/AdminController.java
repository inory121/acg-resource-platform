package com.acg.controller;

import com.acg.common.Result;
import com.acg.entity.User;
import com.acg.service.ResourceService;
import com.acg.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理员功能", description = "管理员专用接口")
public class AdminController {

    private final UserService userService;
    private final ResourceService resourceService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/users")
    @Operation(summary = "获取用户列表", description = "管理员获取用户列表，支持分页和搜索")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        try {
            Page<User> page = new Page<>(current, size);
            IPage<User> result = userService.getUserPage(page, keyword, role, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            userService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/users/{id}/role")
    @Operation(summary = "更新用户角色", description = "设置用户为管理员或普通用户")
    public Result<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        try {
            userService.updateUserRole(id, role);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发资源状态检测
     */
    @PostMapping("/resources/check-status")
    @Operation(summary = "手动触发资源状态检测", description = "立即检测所有资源的可用性")
    public Result<String> triggerResourceStatusCheck() {
        try {
            // 异步执行，避免长时间阻塞请求
            new Thread(resourceService::autoCheckResourceStatus).start();
            return Result.success("已开始后台检测，请稍后刷新查看结果。");
        } catch (Exception e) {
            return Result.error("触发检测失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "获取仪表盘统计", description = "获取管理员仪表盘的统计数据")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取各种统计数据
            stats.put("totalUsers", userService.getTotalUserCount());
            stats.put("totalResources", resourceService.getTotalResourceCount());
            stats.put("totalCategories", resourceService.getTotalCategoryCount());
            stats.put("totalViews", resourceService.getTotalViewCount());
            
            // 获取最近注册的用户
            stats.put("recentUsers", userService.getRecentUsers(5));
            
            // 获取热门资源
            stats.put("hotResources", resourceService.getHotResources(5));
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    @Operation(summary = "获取系统信息", description = "获取系统基本信息")
    public Result<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> info = new HashMap<>();
            info.put("systemName", "ACG资源管理平台");
            info.put("version", "1.0.0");
            info.put("javaVersion", System.getProperty("java.version"));
            info.put("osName", System.getProperty("os.name"));
            info.put("startTime", System.currentTimeMillis());
            
            return Result.success(info);
        } catch (Exception e) {
            return Result.error("获取系统信息失败: " + e.getMessage());
        }
    }
} 