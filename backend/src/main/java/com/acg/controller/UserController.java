package com.acg.controller;

import com.acg.common.Result;
import com.acg.dto.PasswordUpdateDTO;
import com.acg.dto.UserUpdateDTO;
import com.acg.entity.User;
import com.acg.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<Map<String, Object>> getCurrentUserProfile() {
        try {
            Map<String, Object> userInfo = userService.getCurrentUser();
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息", description = "更新当前用户的昵称、头像等信息")
    public Result<Void> updateUserProfile(@RequestBody UserUpdateDTO request) {
        try {
            userService.updateUserProfile(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public Result<Void> updatePassword(@RequestBody PasswordUpdateDTO request) {
        try {
            userService.updatePassword(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Result<User> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.getUserInfo(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被使用")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.isUsernameExists(username);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被使用")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.isEmailExists(email);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}