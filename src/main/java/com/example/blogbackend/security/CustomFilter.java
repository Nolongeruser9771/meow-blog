package com.example.blogbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Lấy ra email trong session
        String email =(String) request.getSession().getAttribute("SESSION");
        if (email == null) {
            //chưa đăng nhập -> chuyển qua filter khác
            filterChain.doFilter(request, response);
            return;
        }

        //Nếu tìm được email -> lấy ra thông tin user
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        //Tạo đối tượng phân quyền
        UsernamePasswordAuthenticationToken  token = new UsernamePasswordAuthenticationToken(
                email,
                null,
                userDetails.getAuthorities() //danh sách quyền
        );

        //Lưu vào security context holder để chuyển qua các bước filter tiếp theo
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request,response);
    }
}
