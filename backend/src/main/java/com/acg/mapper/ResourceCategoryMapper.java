package com.acg.mapper;

import com.acg.entity.ResourceCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资源分类Mapper
 */
@Mapper
public interface ResourceCategoryMapper extends BaseMapper<ResourceCategory> {
    
    /**
     * 获取所有分类（树形结构）
     */
    @Select("SELECT * FROM resource_category WHERE deleted = 0 AND status = 1 ORDER BY sort_order, id")
    List<ResourceCategory> selectAllCategories();
    
    /**
     * 根据父ID获取子分类
     */
    @Select("SELECT * FROM resource_category WHERE parent_id = #{parentId} AND deleted = 0 AND status = 1 ORDER BY sort_order, id")
    List<ResourceCategory> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 检查分类代码是否存在
     */
    @Select("SELECT COUNT(*) FROM resource_category WHERE code = #{code} AND deleted = 0")
    int countByCode(@Param("code") String code);
}