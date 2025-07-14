package com.acg.controller;

import com.acg.common.Result;
import com.acg.entity.ResourceCategory;
import com.acg.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<ResourceCategory>> getAllCategories() {
        try {
            List<ResourceCategory> categories = resourceService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<ResourceCategory> getCategoryById(@PathVariable Long id) {
        try {
            ResourceCategory category = resourceService.getCategoryById(id);
            if (category != null) {
                return Result.success(category);
            } else {
                return Result.error("分类不存在");
            }
        } catch (Exception e) {
            return Result.error("获取分类失败: " + e.getMessage());
        }
    }

    /**
     * 创建新分类
     */
    @PostMapping
    public Result<ResourceCategory> createCategory(@RequestBody ResourceCategory category) {
        try {
            ResourceCategory createdCategory = resourceService.createCategory(category);
            return Result.success(createdCategory);
        } catch (Exception e) {
            return Result.error("创建分类失败: " + e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<ResourceCategory> updateCategory(@PathVariable Long id, @RequestBody ResourceCategory category) {
        try {
            category.setId(id);
            ResourceCategory updatedCategory = resourceService.updateCategory(category);
            if (updatedCategory != null) {
                return Result.success(updatedCategory);
            } else {
                return Result.error("分类不存在");
            }
        } catch (Exception e) {
            return Result.error("更新分类失败: " + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        try {
            boolean deleted = resourceService.deleteCategory(id);
            if (deleted) {
                return Result.success("分类删除成功");
            } else {
                return Result.error("分类不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除分类失败: " + e.getMessage());
        }
    }
} 