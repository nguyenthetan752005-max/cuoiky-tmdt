package com.goodminton.config;

import com.goodminton.entity.User;
import com.goodminton.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Cho phép truy cập trang login mà không cần đăng nhập
        String uri = request.getRequestURI();
        if (uri.equals("/admin/login") || uri.equals("/admin/logout")) {
            return true;
        }

        // Bypass
        return true;
    }
}
