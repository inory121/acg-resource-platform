package com.acg.service.impl;

import com.acg.common.exception.BusinessException;
import com.acg.dto.PasswordUpdateDTO;
import com.acg.dto.RegisterRequest;
import com.acg.dto.UserUpdateDTO;
import com.acg.entity.User;
import com.acg.mapper.UserMapper;
import com.acg.service.UserService;
import com.acg.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        if (isUsernameExists(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (isEmailExists(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.insert(user);
    }
    
    @Override
    public String login(String username, String password) {
        // 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成JWT token
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }
    
    @Override
    public void logout() {
        // 这里可以添加token黑名单逻辑
        // 目前简单实现，实际项目中应该将token加入黑名单
    }
    
    @Override
    public Map<String, Object> getCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取当前用户信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException("未登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = getUserInfo(userId);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("lastLoginTime", user.getLastLoginTime());
        
        return userInfo;
    }
    
    @Override
    @Transactional
    public void updateUserProfile(UserUpdateDTO request) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取当前用户信息");
        }
        
        HttpServletRequest request2 = attributes.getRequest();
        String token = request2.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException("未登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = getUserInfo(userId);
        
        // 更新用户信息
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void updatePassword(PasswordUpdateDTO request) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取当前用户信息");
        }
        
        HttpServletRequest request2 = attributes.getRequest();
        String token = request2.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException("未登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = getUserInfo(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 验证新密码确认
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
    
    @Override
    public User getUserInfoByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userMapper.countByEmail(email) > 0;
    }
    
    // 管理员功能方法实现
    
    @Override
    public IPage<User> getUserPage(Page<User> page, String keyword, String role, Integer status) {
        return userMapper.selectUserPage(page, keyword, role, status);
    }
    
    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = getUserInfo(userId);
        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("无效的角色类型");
        }
        
        User user = getUserInfo(userId);
        user.setRole(role);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserInfo(userId);
        // 逻辑删除
        user.setDeleted(1);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public Long getTotalUserCount() {
        return userMapper.selectCount(null);
    }
    
    @Override
    public List<User> getRecentUsers(int limit) {
        return userMapper.selectRecentUsers(limit);
    }
}