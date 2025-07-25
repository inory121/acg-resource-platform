package com.acg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 资源实体类
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resource")
public class Resource {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源名称
     */
    @TableField("name")
    private String name;

    /**
     * 资源描述
     */
    @TableField("description")
    private String description;

    /**
     * 资源链接
     */
    @TableField("url")
    private String url;

    /**
     * 资源图标URL
     */
    @TableField("icon")
    private String icon;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 标签，逗号分隔
     */
    @TableField("tags")
    private String tags;

    /**
     * 访问次数
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Long likeCount;

    /**
     * 状态：0-禁用，1-无法访问
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序权重，越大越靠前
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建者ID
     */
    @TableField("created_by")
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 最近检测时间
     */
    @TableField("last_check_time")
    private LocalDateTime lastCheckTime;

    /**
     * 连续检测失败次数
     */
    @TableField("check_fail_count")
    private Integer checkFailCount;

    /**
     * 最近检测结果 1-可用 0-异常
     */
    @TableField("last_check_status")
    private Integer lastCheckStatus;
} 