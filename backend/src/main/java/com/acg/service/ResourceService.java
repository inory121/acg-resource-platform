package com.acg.service;

import com.acg.entity.Resource;
import com.acg.entity.ResourceCategory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 资源服务接口
 * 
 * @author ACG Team
 * @since 1.0.0
 */
public interface ResourceService {
    
    /**
     * 分页查询资源列表
     */
    IPage<Resource> getResourcePage(Page<Resource> page, String keyword, Long categoryId);
    
    /**
     * 根据ID获取资源详情
     */
    Resource getResourceById(Long id);
    
    /**
     * 创建资源
     */
    Resource createResource(Resource resource);
    
    /**
     * 更新资源
     */
    Resource updateResource(Long id, Resource resource);
    
    /**
     * 删除资源
     */
    void deleteResource(Long id);
    
    /**
     * 获取热门资源
     */
    List<Resource> getHotResources(int limit);
    
    /**
     * 获取最新资源
     */
    List<Resource> getLatestResources(int limit);
    
    /**
     * 增加资源下载次数
     */
    void incrementDownloadCount(Long id);
    
    /**
     * 增加资源浏览次数
     */
    void incrementViewCount(Long id);
    
    /**
     * 获取所有分类
     */
    List<ResourceCategory> getAllCategories();
    
    /**
     * 根据ID获取分类
     */
    ResourceCategory getCategoryById(Long id);
    
    /**
     * 创建分类
     */
    ResourceCategory createCategory(ResourceCategory category);
    
    /**
     * 更新分类
     */
    ResourceCategory updateCategory(ResourceCategory category);
    
    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);
    
    /**
     * 获取带子分类状态的分类列表
     */
    List<ResourceCategory> getCategoriesWithChildrenStatus(Long parentId);
    
    // 管理员功能方法
    
    /**
     * 获取资源总数
     */
    Long getTotalResourceCount();
    
    /**
     * 获取分类总数
     */
    Long getTotalCategoryCount();
    
    /**
     * 获取总浏览量
     */
    Long getTotalViewCount();
    
    /**
     * 自动（或手动触发）检测所有资源的可用性
     */
    void autoCheckResourceStatus();

    /**
     * 获取资源列表（不分页）
     */
    List<Resource> getResourceList(String keyword, Long categoryId);
}