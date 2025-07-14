package com.acg.config;

import com.acg.common.exception.UnauthorizedException;
import com.acg.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 
 * @author ACG Team
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("未提供有效的认证令牌");
        }
        
        token = token.substring(7);
        
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("认证令牌无效或已过期");
        }
        
        return true;
    }
}