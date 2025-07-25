package com.acg.controller;

import com.acg.common.Result;
import com.acg.entity.Resource;
import com.acg.service.ResourceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public Result<Object> getResourceList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size) {

        if (current != null && size != null) {
            Page<Resource> page = new Page<>(current, size);
            IPage<Resource> pageResult = resourceService.getResourcePage(page, keyword, categoryId);
            return Result.success(pageResult);
        } else {
            List<Resource> listResult = resourceService.getResourceList(keyword, categoryId);
            return Result.success(listResult);
        }
    }

    /**
     * 根据ID获取资源详情
     */
    @GetMapping("/{id}")
    public Result<Resource> getResourceById(@PathVariable Long id) {
        try {
            Resource resource = resourceService.getResourceById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }
            // 只有存在时才增加浏览次数
            resourceService.incrementViewCount(id);
            return Result.success(resource);
        } catch (Exception e) {
            return Result.error("获取资源详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建资源
     */
    @PostMapping
    public Result<Resource> createResource(@RequestBody Resource resource) {
        try {
            Resource createdResource = resourceService.createResource(resource);
            return Result.success(createdResource);
        } catch (Exception e) {
            return Result.error("创建资源失败: " + e.getMessage());
        }
    }

    /**
     * 更新资源
     */
    @PutMapping("/{id}")
    public Result<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        try {
            Resource updatedResource = resourceService.updateResource(id, resource);
            return Result.success(updatedResource);
        } catch (Exception e) {
            return Result.error("更新资源失败: " + e.getMessage());
        }
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return Result.success("资源删除成功");
        } catch (Exception e) {
            return Result.error("删除资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门资源
     */
    @GetMapping("/hot")
    public Result<List<Resource>> getHotResources(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Resource> resources = resourceService.getHotResources(limit);
            return Result.success(resources);
        } catch (Exception e) {
            return Result.error("获取热门资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新资源
     */
    @GetMapping("/latest")
    public Result<List<Resource>> getLatestResources(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Resource> resources = resourceService.getLatestResources(limit);
            return Result.success(resources);
        } catch (Exception e) {
            return Result.error("获取最新资源失败: " + e.getMessage());
        }
    }

    /**
     * 增加下载次数
     */
    @PostMapping("/{id}/download")
    public Result<String> incrementDownloadCount(@PathVariable Long id) {
        try {
            resourceService.incrementDownloadCount(id);
            return Result.success("下载次数增加成功");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
} 