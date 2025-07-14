package com.acg.mapper;

import com.acg.entity.UserHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户历史记录Mapper
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {
}