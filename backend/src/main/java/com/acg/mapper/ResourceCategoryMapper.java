package com.acg.mapper;

import com.acg.entity.ResourceCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源分类Mapper
 */
@Mapper
public interface ResourceCategoryMapper extends BaseMapper<ResourceCategory> {

}