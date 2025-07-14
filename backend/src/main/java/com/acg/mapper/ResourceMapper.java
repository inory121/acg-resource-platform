package com.acg.mapper;

import com.acg.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资源Mapper
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    
    // 暂时注释掉自定义方法，只保留BaseMapper
    @Select("SELECT * FROM v_hot_resources LIMIT #{limit}")
    List<Resource> selectHotResourcesFromView(int limit);
    
    /**
     * 获取总浏览量
     */
    @Select("SELECT COALESCE(SUM(view_count), 0) FROM resource WHERE deleted = 0")
    Long selectTotalViewCount();
}