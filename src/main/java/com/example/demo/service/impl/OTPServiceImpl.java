package com.example.demo.service.impl;

import com.example.demo.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String OTP_PREFIX = "OTP_2FA:";

    @Override
    public void saveOtp(String username, String otpCode) {
        stringRedisTemplate.opsForValue().set(OTP_PREFIX + username, otpCode, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getOtp(String username) {
        return stringRedisTemplate.opsForValue().get(OTP_PREFIX + username);
    }

    @Override
    public void deleteOtp(String username) {
        stringRedisTemplate.delete(OTP_PREFIX + username);
    }
}