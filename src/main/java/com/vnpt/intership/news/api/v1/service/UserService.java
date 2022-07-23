package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;
import com.vnpt.intership.news.api.v1.exception.UserAlreadyExistException;

public interface UserService {
    LoginResponse authentication(LoginRequest loginRequest);

    TokenRefreshResponse refreshToken(String refreshToken);

    UserEntity getCurrentUser();

    UserEntity registerNewUserAccount(RegisterRequest registerRequest) throws UserAlreadyExistException;

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
