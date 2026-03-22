package com.example.demo.controller;

import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.dto.UpdatePassword;
import com.example.demo.model.response.LoginResponse;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtilsToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtilsToken jwtTokenUtils;
    private Map<String, String> getValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));
        }
        userService.register(registerDTO);
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công! Vui lòng kiểm tra email."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));
        }

        LoginResponse loginResponse = userService.login(loginDTO);
        int maxAge = jwtTokenUtils.getRemainingExpiration(loginResponse.getToken());

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getToken())
                .httpOnly(true)
                .path("/")
                .secure(false)
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();

        Map<String, Object> body = new HashMap<>();
        body.put("role", loginResponse.getRole());


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@CookieValue("token") String tempToken, @RequestParam String otpCode) {
        String finalToken = userService.verifyOtp(tempToken, otpCode);

        int maxAge = jwtTokenUtils.getRemainingExpiration(finalToken);
        ResponseCookie cookie = ResponseCookie.from("token", finalToken)
                .httpOnly(true)
                .path("/")
                .secure(false)
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Xác thực 2 lớp thành công!"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePassword updatePassword,
                                            BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));
        }
        userService.updatePassword(updatePassword);
        return ResponseEntity.ok(Map.of("message", "Cập nhật mật khẩu thành công!"));
    }

    @GetMapping("/accept_account")
    public ResponseEntity<?> acceptAccount(@RequestParam(name = "token") String token){
        userService.acceptAccount(token);
        return ResponseEntity.ok(Map.of("message", "Kích hoạt tài khoản thành công!"));
    }

    @GetMapping("/re-send-token")
    public ResponseEntity<?> reSendEmail(@RequestParam String email){
        userService.resendToken(email);
        return ResponseEntity.ok(Map.of("message", "Token đã được gửi lại, vui lòng kiểm tra email!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request){
        UsersEntity user = userService.getProfile(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUsers(request));
    }
}