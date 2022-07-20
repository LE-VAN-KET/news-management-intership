package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.config.security.JwtProvider;
import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.entity.AuthIdentityEntity;
import com.vnpt.intership.news.api.v1.domain.entity.RoleEntity;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import com.vnpt.intership.news.api.v1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public LoginResponse authentication(LoginRequest loginRequest) {
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

            final CompletableFuture<String> jwt = this.jwtProvider.generateJwtToken(authentication, this.jwtExpirationMs);
            final CompletableFuture<String> refreshJwt = this.jwtProvider.generateJwtToken(authentication, this.jwtRefreshExpirationMs);
            CompletableFuture.allOf(jwt, refreshJwt).join();

            // save refresh jwt to database
            saveRefreshToken(refreshJwt.get(), userEntity);
            List<String> roles = userEntity.getRoles().stream().map(r -> r.getRoleName().toString())
                    .collect(Collectors.toList());
            return new LoginResponse(jwt.get(), loginRequest.getUsername(), roles);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Generate Token failed: {}", e.getMessage());
            throw new RuntimeException("Server generate token failed!");
        }
    }

    @Async("asyncExecutor")
    @Transactional(rollbackFor = {Exception.class})
    public void saveRefreshToken(String refreshToken, UserEntity userEntity) {
        AuthIdentityEntity authIdentity = userEntity.getAuthIdentity();
        if (authIdentity == null) {
            authIdentity = new AuthIdentityEntity();

        }
        authIdentity.setRefreshToken(refreshToken);
        userEntity.setAuthIdentity(authIdentity);
        userRepository.save(userEntity);
    }

    private boolean verifyPasswordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}
