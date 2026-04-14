package com.example.demo.controller;

import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.dto.UpdatePassword;
import com.example.demo.model.dto.UserDTO;
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
import java.util.List;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu email"));
        }
        userService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "Đã gửi mật khẩu mới vào email!"));
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

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        if (role == null || role.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu trường 'role'"));
        }
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(Map.of("message", "Cập nhật quyền thành công!"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa người dùng thành công!"));
    }

    @GetMapping("/stats/recent-users")
    public ResponseEntity<List<UserDTO>> getRecentUsers() {
        List<UserDTO> recentUsers = userService.getRecentRegisteredUsers();
        return ResponseEntity.ok(recentUsers);
    }

    @GetMapping("/stats/monthly-users")
    public ResponseEntity<?> getMonthlyUsers(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        java.time.LocalDate now = java.time.LocalDate.now();
        int m = month > 0 ? month : now.getMonthValue();
        int y = year > 0 ? year : now.getYear();
        List<UserDTO> users = userService.getMonthlyRegisteredUsers(m, y);
        return ResponseEntity.ok(Map.of(
                "month", m,
                "year", y,
                "count", users.size(),
                "users", users
        ));
    }
}