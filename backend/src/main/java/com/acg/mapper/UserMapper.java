package com.acg.mapper;

import com.acg.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}