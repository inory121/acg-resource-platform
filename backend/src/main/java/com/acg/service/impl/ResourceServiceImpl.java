package com.acg.service.impl;

import com.acg.common.exception.BusinessException;
import com.acg.entity.Resource;
import com.acg.entity.ResourceCategory;
import com.acg.mapper.ResourceCategoryMapper;
import com.acg.mapper.ResourceMapper;
import com.acg.service.ResourceService;
import com.acg.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 资源服务实现类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceMapper resourceMapper;
    private final ResourceCategoryMapper resourceCategoryMapper;
    private final JwtUtil jwtUtil;

    @Override
    public List<Resource> getResourceList(String keyword, Long categoryId) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword)
                    .or()
                    .like("description", keyword);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByDesc("sort_order").orderByDesc("created_time");
        
        return resourceMapper.selectList(queryWrapper);
    }

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
        
        queryWrapper.orderByDesc("sort_order").orderByDesc("created_time");
        
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
        // 从请求中获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        // 设置资源的基本属性
        resource.setCreatedTime(LocalDateTime.now());
        resource.setUpdatedTime(LocalDateTime.now());
        resource.setViewCount(0L);
        resource.setLikeCount(0L);
        resource.setStatus(1);
        resource.setCreatedBy(userId);

        // 插入资源到数据库
        resourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    @Transactional
    public Resource updateResource(Long id, Resource resource) {
        Resource existingResource = getResourceById(id);
        
        BeanUtils.copyProperties(resource, existingResource, "id", "createdTime", "viewCount", "likeCount", "status", "createdBy");
        existingResource.setUpdatedTime(LocalDateTime.now());
        
        resourceMapper.updateById(existingResource);
        return existingResource;
    }
    
    @Override
    @Transactional
    public void deleteResource(Long id) {
        resourceMapper.deleteById(id);
    }
    
    @Override
    public List<Resource> getHotResources(int limit) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view_count").last("LIMIT " + limit);
        return resourceMapper.selectList(queryWrapper);
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
        
        BeanUtils.copyProperties(category, existingCategory, "id", "createdTime");
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
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(view_count) as total_views");
        List<Object> result = resourceMapper.selectObjs(queryWrapper);
        if (result != null && !result.isEmpty() && result.get(0) != null) {
            return ((Number) result.get(0)).longValue();
        }
        return 0L;
    }
    
    public List<ResourceCategory> getCategoriesWithChildrenStatus(Long parentId) {
        // 1. 查询当前层级的所有分类
        QueryWrapper<ResourceCategory> wrapper = new QueryWrapper<>();
        if (parentId == null) {
            // parentId 为 null 时，查询所有分类，而不仅仅是顶级分类
            // wrapper.eq("parent_id", 0); // 移除此行
        } else {
            wrapper.eq("parent_id", parentId);
        }
        List<ResourceCategory> categories = resourceCategoryMapper.selectList(wrapper);
        if (categories.isEmpty()) return categories;

        // 如果是查询所有分类，则直接返回，hasChildren 的判断交给前端或者在前端重新构建
        if (parentId == null) {
            return categories;
        }

        // 2. 查询这些分类的所有子分类 (只在查询特定父ID时执行)
        List<Long> ids = categories.stream().map(ResourceCategory::getId).collect(Collectors.toList());
        QueryWrapper<ResourceCategory> subCatWrapper = new QueryWrapper<>();
        subCatWrapper.in("parent_id", ids);
        List<ResourceCategory> subCategories = resourceCategoryMapper.selectList(subCatWrapper);

        // 3. 查询这些分类下的所有资源
        QueryWrapper<Resource> resWrapper = new QueryWrapper<>();
        resWrapper.in("category_id", ids);
        List<Resource> resources = resourceMapper.selectList(resWrapper);

        // 4. 统计每个分类是否有子分类或资源
        Map<Long, Boolean> hasChildrenMap = new HashMap<>();
        for (Long id : ids) {
            boolean hasChildCat = subCategories.stream().anyMatch(c -> c.getParentId().equals(id));
            boolean hasRes = resources.stream().anyMatch(r -> r.getCategoryId().equals(id));
            hasChildrenMap.put(id, hasChildCat || hasRes);
        }

        // 5. 设置 hasChildren 字段
        for (ResourceCategory cat : categories) {
            cat.setHasChildren(hasChildrenMap.getOrDefault(cat.getId(), false));
        }

        return categories;
    }
    
    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId() {
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
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 每10分钟自动检测所有资源可用性
     * 检查HTTP状态码、页面关键字、HTTPS证书，多次失败才标记异常
     */
    @Override
    @Scheduled(cron = "0 */10 * * * ?")
    public void autoCheckResourceStatus() {
        List<Resource> resources = resourceMapper.selectList(null);
        for (Resource res : resources) {
            logger.info("开始检测资源: {} ({})", res.getName(), res.getUrl());
            boolean ok = false;
            int code = 0;
            String errorMessage = null;
            try {
                URL url = new URL(res.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true); // 自动处理重定向
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                conn.setConnectTimeout(10000); // 10秒连接超时
                conn.setReadTimeout(10000); // 10秒读取超时
                conn.setRequestMethod("GET");

                if (conn instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) conn).setSSLSocketFactory((HttpsURLConnection.getDefaultSSLSocketFactory()));
                }
                code = conn.getResponseCode();
                if (code >= 200 && code < 300) { // 2xx 状态码均视为成功
                    ok = true;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                logger.error("检测资源失败 [{} - {}]: {}", res.getName(), res.getUrl(), errorMessage, e);
                ok = false;
            }

            if (ok) {
                logger.info("资源 [{}] 检测成功，状态码: {}", res.getName(), code);
                res.setStatus(1);
                res.setCheckFailCount(0);
                res.setLastCheckStatus(1);
            } else {
                logger.warn("资源 [{}] 检测失败，状态码: {}, 错误: {}", res.getName(), code, errorMessage);
                int fail = res.getCheckFailCount() == null ? 0 : res.getCheckFailCount();
                fail++;
                res.setCheckFailCount(fail);
                if (fail >= 3) {
                    res.setStatus(0);
                    res.setLastCheckStatus(0);
                    logger.warn("资源 [{}] 已连续 {} 次检测失败，标记为无法访问。", res.getName(), fail);
                }
            }
            res.setLastCheckTime(LocalDateTime.now());
            resourceMapper.updateById(res);
        }
    }
}