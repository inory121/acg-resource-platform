package com.acg.service.impl;

import com.acg.entity.UserHistory;
import com.acg.mapper.UserHistoryMapper;
import com.acg.service.HistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史记录服务实现类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    
    private final UserHistoryMapper userHistoryMapper;
    
    @Override
    @Transactional
    public void addHistory(Long userId, Long resourceId) {
        // 先删除已存在的记录
        QueryWrapper<UserHistory> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId)
                .eq("resource_id", resourceId);
        userHistoryMapper.delete(deleteWrapper);
        
        // 添加新记录
        UserHistory history = new UserHistory();
        history.setUserId(userId);
        history.setResourceId(resourceId);
        history.setVisitTime(LocalDateTime.now());
        
        userHistoryMapper.insert(history);
    }
    
    @Override
    public IPage<UserHistory> getUserHistory(Page<UserHistory> page, Long userId) {
        QueryWrapper<UserHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("visit_time");
        
        return userHistoryMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    @Transactional
    public void clearUserHistory(Long userId) {
        QueryWrapper<UserHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        userHistoryMapper.delete(queryWrapper);
    }
    
    @Override
    @Transactional
    public void deleteHistory(Long userId, Long resourceId) {
        QueryWrapper<UserHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("resource_id", resourceId);
        
        userHistoryMapper.delete(queryWrapper);
    }
    
    @Override
    public List<Long> getUserRecentResourceIds(Long userId, int limit) {
        QueryWrapper<UserHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("visit_time")
                .last("LIMIT " + limit);
        
        List<UserHistory> histories = userHistoryMapper.selectList(queryWrapper);
        return histories.stream()
                .map(UserHistory::getResourceId)
                .collect(Collectors.toList());
    }
}