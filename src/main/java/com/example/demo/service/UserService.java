package com.example.demo.service;

import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.dto.UpdatePassword;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    void register(RegisterDTO register);
    LoginResponse login(LoginDTO login);
    void updatePassword(UpdatePassword updatePassword);
    void acceptAccount(String token);
    void resendToken(String email);
    UsersEntity getProfile(HttpServletRequest request);
    List<UserDTO> getUsers(HttpServletRequest request);
    String verifyOtp(String tempToken, String otpCode);
}
