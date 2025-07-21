package com.acg.mapper;

import com.acg.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 资源Mapper
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    
}