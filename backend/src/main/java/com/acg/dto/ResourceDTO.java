package com.acg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 资源DTO
 */
@Data
public class ResourceDTO {
    
    @NotBlank(message = "资源名称不能为空")
    private String name;
    
    private String description;
    
    @NotBlank(message = "资源链接不能为空")
    private String url;
    
    private String icon;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    private String tags;

    // 排序权重，越大越靠前
    private Integer sortOrder;
}