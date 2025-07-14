package com.acg.service;

import com.acg.dto.PasswordUpdateDTO;
import com.acg.dto.RegisterRequest;
import com.acg.dto.UserUpdateDTO;
import com.acg.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    void register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 用户登出
     */
    void logout();
    
    /**
     * 获取当前用户信息
     */
    Map<String, Object> getCurrentUser();
    
    /**
     * 更新用户信息
     */
    void updateUserProfile(UserUpdateDTO request);
    
    /**
     * 修改密码
     */
    void updatePassword(PasswordUpdateDTO request);
    
    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);
    
    /**
     * 根据用户名获取用户信息
     */
    User getUserInfoByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);
    
    // 管理员功能方法
    
    /**
     * 分页获取用户列表
     */
    IPage<User> getUserPage(Page<User> page, String keyword, String role, Integer status);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);
    
    /**
     * 更新用户角色
     */
    void updateUserRole(Long userId, String role);
    
    /**
     * 删除用户
     */
    void deleteUser(Long userId);
    
    /**
     * 获取用户总数
     */
    Long getTotalUserCount();
    
    /**
     * 获取最近注册的用户
     */
    List<User> getRecentUsers(int limit);
}