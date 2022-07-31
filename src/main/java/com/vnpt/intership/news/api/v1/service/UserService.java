package com.vnpt.intership.news.api.v1.service;

import com.vnpt.intership.news.api.v1.domain.dto.User;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;

import java.util.List;

import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;
import com.vnpt.intership.news.api.v1.exception.UserAlreadyExistException;

public interface UserService {
    LoginResponse authentication(LoginRequest loginRequest, DeviceMeta deviceMeta);

    TokenRefreshResponse refreshToken(String refreshToken, DeviceMeta deviceMeta);

    UserEntity getCurrentUser();

    UserEntity registerNewUserAccount(RegisterRequest registerRequest) throws UserAlreadyExistException;

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    void updateRefreshTokenByUsername(String username, DeviceMeta deviceMeta);

    TokenRefreshResponse resetPassword(String passwordNew, DeviceMeta deviceMeta);

    List<UserEntity> findAll();

    User findById(String id);

    void deleteById(String id);

    User updateUser(String id, User user);

}
