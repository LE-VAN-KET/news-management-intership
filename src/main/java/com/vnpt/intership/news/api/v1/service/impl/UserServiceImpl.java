package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.common.UserRole;
import com.vnpt.intership.news.api.v1.config.security.JwtProvider;
import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;
import com.vnpt.intership.news.api.v1.domain.entity.AuthIdentityEntity;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.exception.*;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import com.vnpt.intership.news.api.v1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;
import com.vnpt.intership.news.api.v1.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${security.jwt.token.expirationMs}")
    private int jwtExpirationMs;

    @Value("${security.jwt.refreshtoken.expirationMs}")
    private int jwtRefreshExpirationMs;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public LoginResponse authentication(LoginRequest loginRequest, DeviceMeta deviceMeta) {
        try {
            UserEntity userEntity = userRepository.findByUsername(loginRequest.getUsername().trim())
                    .orElseThrow(() -> new UserNotFoundException("Username or password wrong!"));

            // verify password
            boolean validPassword = verifyPasswordMatches(loginRequest.getPassword(), userEntity.getPassword());
            if (!validPassword) {
                throw  new UserNotFoundException("Username or password wrong!");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername().trim(),
                            loginRequest.getPassword().trim()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Map<String, String> res = generatingAccessTokenAndRefreshToken(authentication, userEntity, deviceMeta);

            List<String> roles = userEntity.getRoles().stream().map(r -> r.getRoleName().toString())
                    .collect(Collectors.toList());
            return new LoginResponse(res.get("jwt"), res.get("refreshJwt"), loginRequest.getUsername(), roles);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Generate Token failed: {}", e.getMessage());
            throw new RuntimeException("Server generate token failed!");
        }
    }

    @Async("asyncExecutor")
    @Transactional(rollbackFor = {Exception.class})
    public void saveRefreshToken(String refreshToken, UserEntity userEntity, DeviceMeta deviceMeta) {
        AuthIdentityEntity authIdentity = userEntity.getAuthIdentity();
        if (authIdentity == null) {
            authIdentity = new AuthIdentityEntity();
        } else {
            Set<DeviceMeta> deviceMetas = authIdentity.getDeviceMetas().stream()
                    .map(device -> {
                        if (device.getLocation().equals(deviceMeta.getLocation())
                                && device.getDeviceDetails().equals(deviceMeta.getDeviceDetails())) {
                            device.setRefreshToken(refreshToken);
                        }
                        return device;
                    }).collect(Collectors.toSet());
            authIdentity.setDeviceMetas(deviceMetas);
        }

        deviceMeta.setRefreshToken(refreshToken);
        authIdentity.getDeviceMetas().add(deviceMeta);

//        authIdentity.setRefreshToken(refreshToken);
        userEntity.setAuthIdentity(authIdentity);
        userRepository.save(userEntity);
    }

    private boolean verifyPasswordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public TokenRefreshResponse refreshToken(String refreshToken, DeviceMeta deviceMeta) {
        try {
            deviceMeta.setRefreshToken(refreshToken);
            UserEntity userEntity = userRepository.findByDeviceMeta(deviceMeta)
                    .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database!"));

            // validate expire refresh token
            boolean isExpireRefreshToken = this.jwtProvider.isTokenExpired(refreshToken);
            if (isExpireRefreshToken) {
                throw new TokenException("Refresh token was expired. Please make a new login request");
            }

            // generating a new access token and refresh token
            Map<String, String> res = generatingAccessTokenAndRefreshToken(null, userEntity, deviceMeta);
            return new TokenRefreshResponse(res.get("jwt"), res.get("refreshJwt"));
        } catch (InterruptedException | ExecutionException e) {
            log.error("Generate Token failed: {}", e.getMessage());
            throw new RuntimeException("Server generate token failed!");
        }
    }

    private Map<String, String> generatingAccessTokenAndRefreshToken(Authentication authentication, UserEntity userEntity,
                                                                     DeviceMeta deviceMeta)
            throws ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap();
        CompletableFuture<String> jwt;
        CompletableFuture<String> refreshJwt;
        if (authentication != null) {
            jwt = this.jwtProvider.generateJwtToken(authentication, this.jwtExpirationMs);
            refreshJwt = this.jwtProvider.generateJwtToken(authentication, this.jwtRefreshExpirationMs);
        } else {
            jwt = this.jwtProvider.generateJwtToken(userEntity, this.jwtExpirationMs);
            refreshJwt = this.jwtProvider.generateJwtToken(userEntity, this.jwtRefreshExpirationMs);
        }

        CompletableFuture.allOf(jwt, refreshJwt).join();

        // save refresh jwt to database
        saveRefreshToken(refreshJwt.get(), userEntity, deviceMeta);
        response.put("jwt", jwt.get());
        response.put("refreshJwt", refreshJwt.get());
        return response;
    }

    @Override
    public UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UnAuthorizationException("User Unauthorized"));
    }

    @Override
    public UserEntity registerNewUserAccount(RegisterRequest registerRequest){
        if (existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistException("Username already exists");
        };

        if (existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistException("Email already exists");
        };

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerRequest.getUsername());
        userEntity.setEmail(registerRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        RoleEntity roleEntity = roleRepository.findByRoleName(UserRole.ROLE_USER.toString())
                        .orElseThrow(() -> new RoleNotFoundException("Role User not found"));
        userEntity.setRoles(Set.of(roleEntity));

        return userRepository.save(userEntity);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
