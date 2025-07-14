package com.acg.mapper;

import com.acg.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);
    
    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND deleted = 0")
    int countByEmail(@Param("email") String email);
    
    /**
     * 分页查询用户列表（管理员功能）
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            "AND role = #{role} " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY created_time DESC" +
            "</script>")
    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword, @Param("role") String role, @Param("status") Integer status);
    
    /**
     * 获取最近注册的用户
     */
    @Select("SELECT * FROM user WHERE deleted = 0 ORDER BY created_time DESC LIMIT #{limit}")
    List<User> selectRecentUsers(@Param("limit") int limit);
}