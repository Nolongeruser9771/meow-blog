package com.example.blogbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true //(role allowed)
)
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final CustomFilter customFilter;

    //Tạo đối tượng Password Encoder (cc pp so sánh và mã hóa mật khẩu)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Tạo đối tượng AuthenticationProvider, set các chức năng mã hóa mật khẩu và tìm kiếm user
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    //Tạo đối tượng AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //List route công khai, ai cũng vào được
        String[] PUBLIC_ROUTE ={"/", "/login","/api/v1/auth/**","/admin-lte/**","/assets/**","/css/**","/img/**", "/js/**","/vendor/**",
        "/search/**","/categories/**", "/category/**", "/blogs/**", "api/v1/files","/api/v1/files/**"};
        String[] ADMIN_ROUTE= {"/admin/blogs", "/admin/categories/**","/api/v1/admin/category/**", "api/v1/admin/categories/**"};
        http
                .csrf(c -> c.disable()) //Disable bảo vệ trước tấn công ủy quyền
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(PUBLIC_ROUTE).permitAll()
                        .requestMatchers(ADMIN_ROUTE).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        //Xoá cookie và cho hết hạn session
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider()) //cung cấp pp xác thực username, password, tìm kiểm user
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
