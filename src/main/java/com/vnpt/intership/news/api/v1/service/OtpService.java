package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;

public interface OtpService {
    void sendOtp(String username);
    TokenRefreshResponse validateOtp(String key, String otp);
}
