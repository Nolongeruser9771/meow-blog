package com.example.blogbackend.controller;

import com.example.blogbackend.request.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login-handle")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        //Tạo đối tượng xác thực, làm tham số đầu vào cho AuthenticationManager
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        try {
            //Tiến hành xác thực (gọi phương thức tương ứng trong AuthenticationManager)
            Authentication authentication = authenticationManager.authenticate(token);

            //Lưu đối tượng xác thực vào security context holder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Lưu đối tượng xác thực vào session ̣lưu email/username
            httpSession.setAttribute("SESSION", authentication.getName());

            return ResponseEntity.ok().body(HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.ok().body(HttpStatus.BAD_REQUEST);
        }
    }
}
