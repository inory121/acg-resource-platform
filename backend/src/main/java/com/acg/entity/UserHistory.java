package com.acg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户历史实体
 */
@Data
@TableName("user_history")
public class UserHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long resourceId;
    
    private LocalDateTime visitTime;
    
    // 扩展字段，用于显示资源信息
    private String resourceName;
    private String description;
    private String url;
    private String icon;
    private Long viewCount;
    private Long likeCount;
    private String categoryName;
}