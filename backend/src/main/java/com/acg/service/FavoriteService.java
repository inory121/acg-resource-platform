package com.acg.service;

import com.acg.entity.UserFavorite;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 收藏服务接口
 * 
 * @author ACG Team
 * @since 1.0.0
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long resourceId);
    
    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long resourceId);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long resourceId);
    
    /**
     * 获取用户收藏列表
     */
    IPage<UserFavorite> getUserFavorites(Page<UserFavorite> page, Long userId);
    
    /**
     * 获取用户收藏的资源ID列表
     */
    List<Long> getUserFavoriteResourceIds(Long userId);
    
    /**
     * 获取资源收藏数量
     */
    Long getResourceFavoriteCount(Long resourceId);
}