package com.acg.mapper;

import com.acg.entity.UserFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户收藏Mapper
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    
    /**
     * 分页查询用户收藏
     */
    @Select("SELECT f.*, r.name as resourceName, r.description, r.url, r.icon, r.view_count, r.like_count, " +
            "c.name as categoryName FROM user_favorite f " +
            "LEFT JOIN resource r ON f.resource_id = r.id " +
            "LEFT JOIN resource_category c ON r.category_id = c.id " +
            "WHERE f.user_id = #{userId} AND r.deleted = 0 " +
            "ORDER BY f.created_time DESC")
    IPage<UserFavorite> selectUserFavorites(Page<UserFavorite> page, @Param("userId") Long userId);
}