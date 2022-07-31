package com.vnpt.intership.news.api.v1.controller;

import com.vnpt.intership.news.api.v1.domain.dto.request.*;
import com.vnpt.intership.news.api.v1.domain.dto.response.LoginResponse;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;

import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.event.OnSendOtpResetPasswordEvent;
import com.vnpt.intership.news.api.v1.exception.OTPException;
import com.vnpt.intership.news.api.v1.exception.TooManyRequestException;
import com.vnpt.intership.news.api.v1.service.DeviceService;
import com.vnpt.intership.news.api.v1.service.OtpService;
import com.vnpt.intership.news.api.v1.service.UserService;
import com.vnpt.intership.news.api.v1.util.ratelimit.LimitApiEndpoint;
import com.vnpt.intership.news.api.v1.util.ratelimit.LimitVerifyPerOtp;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisClusterCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
@SecurityRequirement(name = "BearerAuth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Value("${com.app.JWT_AUTH_HEADER}")
    private String jwtAuthHeader;

    @Value("${com.app.token.prefix}")
    private String tokenPrefix;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OtpService otpService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${MAX_REQUEST_VALIDATE_PER_OTP}")
    private int MAX_REQUEST_VALIDATE_PER_OTP;

    @Value("${prefixOtpCountHit}")
    private String prefixOtpCountHit;

    @Value("${prefixApi}")
    private String prefixApi;

    @Value("${MAX_REQUEST_SEND_OTP_PER_HOURS}")
    private int MAX_REQUEST_SEND_OTP_PER_HOURS;

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
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            DeviceMeta deviceMeta = deviceService.extractDevice(request);
            // delete refresh token
            userService.updateRefreshTokenByUsername(user.getUsername(), deviceMeta);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody OtpRequest otpRequest, HttpSession session) {
        String username = otpRequest.getUsername().trim();

        rateLimitedApi(prefixApi + "forgotPassword:" + username, this.MAX_REQUEST_SEND_OTP_PER_HOURS,
                "Too many request, Allowed " + this.MAX_REQUEST_SEND_OTP_PER_HOURS +
                        " request send OTP per hours. Please try after some time.");

        session.setAttribute("username", username);
        eventPublisher.publishEvent(new OnSendOtpResetPasswordEvent(this, username));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyCodeOtp(@Valid @RequestBody OtpVerifyRequest otpVerifyRequest,
                                           HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new OTPException("Session expired! Please resend OTP against!");
        }

        try {
            this.redisTemplate.execute(LimitVerifyPerOtp.of(username, this.MAX_REQUEST_VALIDATE_PER_OTP,
                    this.prefixOtpCountHit));
        } catch (Exception e) {
            throw new TooManyRequestException("Allowed 5 request verify per OTP! Please resend renew OTP against.");
        }

        TokenRefreshResponse resp = otpService.validateOtp(username, otpVerifyRequest.getOtp());
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
                                           HttpServletRequest request) {
        DeviceMeta deviceMeta = deviceService.extractDevice(request);
        TokenRefreshResponse resp = userService.resetPassword(resetPasswordRequest.getPassword(),
                deviceMeta);
        return ResponseEntity.status(200).body(resp);
    }

    private void rateLimitedApi(String key, int maxRequest, String msgException) {
        try {

            this.redisTemplate.execute(LimitApiEndpoint.builder().key(key)
                            .bucketCapacity(maxRequest).timeWindowInMilliSeconds(60*60*60).build());
        } catch (TooManyRequestException e) {
            throw new TooManyRequestException(msgException);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
