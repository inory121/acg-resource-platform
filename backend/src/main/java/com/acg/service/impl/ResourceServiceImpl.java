package com.acg.service.impl;

import com.acg.common.exception.BusinessException;
import com.acg.entity.Resource;
import com.acg.entity.ResourceCategory;
import com.acg.mapper.ResourceCategoryMapper;
import com.acg.mapper.ResourceMapper;
import com.acg.service.ResourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源服务实现类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    
    private final ResourceMapper resourceMapper;
    private final ResourceCategoryMapper resourceCategoryMapper;
    
    @Override
    public IPage<Resource> getResourcePage(Page<Resource> page, String keyword, Long categoryId) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword)
                    .or()
                    .like("description", keyword);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByDesc("created_time");
        
        return resourceMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public Resource getResourceById(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        return resource;
    }
    
    @Override
    @Transactional
    public Resource createResource(Resource resource) {
        resource.setCreatedTime(LocalDateTime.now());
        resource.setUpdatedTime(LocalDateTime.now());
        resource.setViewCount(0L);
        resource.setLikeCount(0L);
        resource.setStatus(1);
        
        resourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    @Transactional
    public Resource updateResource(Long id, Resource resource) {
        Resource existingResource = getResourceById(id);
        
        existingResource.setName(resource.getName());
        existingResource.setDescription(resource.getDescription());
        existingResource.setCategoryId(resource.getCategoryId());
        existingResource.setUrl(resource.getUrl());
        existingResource.setIcon(resource.getIcon());
        existingResource.setTags(resource.getTags());
        existingResource.setUpdatedTime(LocalDateTime.now());
        
        resourceMapper.updateById(existingResource);
        return existingResource;
    }
    
    @Override
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = getResourceById(id);
        resourceMapper.deleteById(id);
    }
    
    @Override
    public List<Resource> getHotResources(int limit) {
        return resourceMapper.selectHotResourcesFromView(limit);
    }
    
    @Override
    public List<Resource> getLatestResources(int limit) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_time")
                .last("LIMIT " + limit);
        
        return resourceMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        // Resource实体类没有downloadCount字段，暂时跳过
        Resource resource = getResourceById(id);
        resource.setUpdatedTime(LocalDateTime.now());
        resourceMapper.updateById(resource);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Resource resource = getResourceById(id);
        resource.setViewCount(resource.getViewCount() + 1);
        resource.setUpdatedTime(LocalDateTime.now());
        resourceMapper.updateById(resource);
    }
    
    @Override
    public List<ResourceCategory> getAllCategories() {
        return resourceCategoryMapper.selectList(null);
    }
    
    @Override
    public ResourceCategory getCategoryById(Long id) {
        return resourceCategoryMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public ResourceCategory createCategory(ResourceCategory category) {
        category.setCreatedTime(LocalDateTime.now());
        category.setUpdatedTime(LocalDateTime.now());
        resourceCategoryMapper.insert(category);
        return category;
    }
    
    @Override
    @Transactional
    public ResourceCategory updateCategory(ResourceCategory category) {
        ResourceCategory existingCategory = getCategoryById(category.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setUpdatedTime(LocalDateTime.now());
        
        resourceCategoryMapper.updateById(existingCategory);
        return existingCategory;
    }
    
    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        ResourceCategory category = getCategoryById(id);
        if (category == null) {
            return false;
        }
        
        // 检查是否有资源使用此分类
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", id);
        long count = resourceMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("该分类下还有资源，无法删除");
        }
        
        resourceCategoryMapper.deleteById(id);
        return true;
    }
    
    // 管理员功能方法实现
    
    @Override
    public Long getTotalResourceCount() {
        return resourceMapper.selectCount(null);
    }
    
    @Override
    public Long getTotalCategoryCount() {
        return resourceCategoryMapper.selectCount(null);
    }
    
    @Override
    public Long getTotalViewCount() {
        return resourceMapper.selectTotalViewCount();
    }
}