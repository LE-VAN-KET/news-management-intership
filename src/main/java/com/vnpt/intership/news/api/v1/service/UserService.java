package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import org.springframework.security.core.Authentication;

public interface UserService {
    LoginResponse authentication(LoginRequest loginRequest);
    UserEntity save(UserEntity userEntity);

    TokenRefreshResponse refreshToken(String refreshToken);
}
