package com.acg.service;

import com.acg.entity.UserHistory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 历史记录服务接口
 * 
 * @author ACG Team
 * @since 1.0.0
 */
public interface HistoryService {
    
    /**
     * 添加浏览历史
     */
    void addHistory(Long userId, Long resourceId);
    
    /**
     * 获取用户浏览历史
     */
    IPage<UserHistory> getUserHistory(Page<UserHistory> page, Long userId);
    
    /**
     * 清空用户浏览历史
     */
    void clearUserHistory(Long userId);
    
    /**
     * 删除指定历史记录
     */
    void deleteHistory(Long userId, Long resourceId);
    
    /**
     * 获取用户最近浏览的资源ID列表
     */
    List<Long> getUserRecentResourceIds(Long userId, int limit);
}