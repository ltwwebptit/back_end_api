package com.example.demo.utils;

import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtarctUserUtils {
    private final UserRepository  userRepository;
    private  final JwtUtilsToken token1;

    public UsersEntity extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        String username = token1.extractUsernameFromToken(token);
        UsersEntity users = userRepository.findByUsernameAndStatus(username,1).get();
        return users;
    }
}
