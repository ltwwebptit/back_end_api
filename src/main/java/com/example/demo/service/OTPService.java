package com.example.demo.service;

public interface OTPService {
    public void saveOtp(String username, String otpCode);
    public String getOtp(String username);
    public void deleteOtp(String username);
}
