package com.vnpt.intership.news.api.v1.controller;


import com.vnpt.intership.news.api.v1.domain.dto.request.LoginRequest;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Value("${com.app.JWT_AUTH_HEADER}")
    private String jwtAuthHeader;

    @Value("${com.app.token.prefix}")
    private String tokenPrefix;

    @PostMapping("/login")
    public LoginResponse signIn(@Valid @RequestBody LoginRequest loginRequest,
                                HttpServletResponse response) {
        LoginResponse loginResponse = userService.authentication(loginRequest);
        response.addHeader(jwtAuthHeader, tokenPrefix + " " + loginResponse.getAccessToken());
        return loginResponse;
    }

}
