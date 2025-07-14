package com.acg.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO {
    
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;
    
    private String avatar;
}