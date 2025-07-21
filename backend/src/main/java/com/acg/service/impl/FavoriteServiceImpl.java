package com.acg.service.impl;

import com.acg.common.exception.BusinessException;
import com.acg.entity.UserFavorite;
import com.acg.mapper.UserFavoriteMapper;
import com.acg.service.FavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    
    private final UserFavoriteMapper userFavoriteMapper;
    
    @Override
    @Transactional
    public void addFavorite(Long userId, Long resourceId) {
        // 检查是否已收藏
        if (isFavorited(userId, resourceId)) {
            throw new BusinessException("已经收藏过该资源");
        }
        
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setResourceId(resourceId);
        favorite.setCreatedTime(LocalDateTime.now());
        
        userFavoriteMapper.insert(favorite);
    }
    
    @Override
    @Transactional
    public void removeFavorite(Long userId, Long resourceId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("resource_id", resourceId);
        
        userFavoriteMapper.delete(queryWrapper);
    }
    
    @Override
    public boolean isFavorited(Long userId, Long resourceId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("resource_id", resourceId);
        
        return userFavoriteMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public IPage<UserFavorite> getUserFavorites(Page<UserFavorite> page, Long userId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("created_time");
        
        return userFavoriteMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public List<Long> getUserFavoriteResourceIds(Long userId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .select("resource_id");
        
        List<UserFavorite> favorites = userFavoriteMapper.selectList(queryWrapper);
        return favorites.stream()
                .map(UserFavorite::getResourceId)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getResourceFavoriteCount(Long resourceId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        
        return userFavoriteMapper.selectCount(queryWrapper);
    }
}