package com.example.demo.filter;

import com.example.demo.entity.UsersEntity;
import com.example.demo.utils.JwtUtilsToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException; // CHÚ Ý: Chỉ dùng IOException chuẩn của Java

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtilsToken jwtUtilsToken;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        try {
            String token = null;
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token != null) {
                String username = jwtUtilsToken.extractUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsersEntity userDetails = (UsersEntity) userDetailsService.loadUserByUsername(username);

                    if (jwtUtilsToken.validateToken(token, userDetails)) {


                        boolean is2faPassed = jwtUtilsToken.extract2faStatus(token);
                        if (userDetails.isTwoFactorEnabled() && !is2faPassed) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vui lòng xác thực 2 lớp (OTP) để tiếp tục");
                            return;
                        }


                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi xác thực JWT: ", e);
        }

        chain.doFilter(request, response);
    }
}