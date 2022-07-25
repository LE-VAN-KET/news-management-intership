package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.request.TokenRefreshRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;

import com.vnpt.intership.news.api.v1.domain.dto.request.RegisterRequest;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.service.DeviceService;
import com.vnpt.intership.news.api.v1.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Value("${com.app.JWT_AUTH_HEADER}")
    private String jwtAuthHeader;

    @Value("${com.app.token.prefix}")
    private String tokenPrefix;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/login")
    public LoginResponse signIn(@Valid @RequestBody LoginRequest loginRequest,
                                HttpServletResponse response, HttpServletRequest request) {
        DeviceMeta deviceMeta = deviceService.extractDevice(request);
        LoginResponse loginResponse = userService.authentication(loginRequest, deviceMeta);
        response.addHeader(jwtAuthHeader, tokenPrefix + " " + loginResponse.getAccessToken());
        return loginResponse;
    }

    @PostMapping("/refresh-token")
    public TokenRefreshResponse refreshToken(@Valid @RequestBody TokenRefreshRequest refreshRequest,
                                      HttpServletResponse response, HttpServletRequest request) {
        DeviceMeta deviceMeta = deviceService.extractDevice(request);
        TokenRefreshResponse tokenRefreshResponse = userService.refreshToken(refreshRequest.getRefreshToken(), deviceMeta);
        response.addHeader(jwtAuthHeader, tokenPrefix + " " + tokenRefreshResponse.getAccessToken());
        return tokenRefreshResponse;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){

        userService.registerNewUserAccount(registerRequest);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }

    @GetMapping("/logout")
    @SecurityRequirement(name = "BearerAuth")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserEntity userEntity = userService.getCurrentUser();
            DeviceMeta deviceMeta = deviceService.extractDevice(request);
            // delete refresh token
            userService.updateRefreshTokenByUsername(userEntity.getUsername(), deviceMeta);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

}
