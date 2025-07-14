package com.acg.controller;

import com.acg.common.Result;
import com.acg.dto.LoginRequest;
import com.acg.dto.RegisterRequest;
import com.acg.service.UserService;
import com.acg.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册、登出等接口")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            // 登录时直接查用户信息返回
            result.put("user", userService.getUserInfoByUsername(request.getUsername()));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public Result<Void> logout() {
        try {
            userService.logout();
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户信息")
    public Result<Map<String, Object>> getCurrentUser() {
        try {
            Map<String, Object> userInfo = userService.getCurrentUser();
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}