package com.vnpt.intership.news.api.v1.event.listener;

import com.vnpt.intership.news.api.v1.event.OnClearOtpResetPasswordEvent;
import com.vnpt.intership.news.api.v1.event.OnSendOtpResetPasswordEvent;
import com.vnpt.intership.news.api.v1.repository.CustomUserRepository;
import com.vnpt.intership.news.api.v1.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SendOtpResetPasswordListener {
    @Autowired
    private OtpService otpService;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Async
    @EventListener
    public void sendOtpConfirmResetPassword(OnSendOtpResetPasswordEvent event) {
        String username = event.getUsername();
        otpService.sendOtp(username);
    }

    @Async
    @EventListener
    public void clearOtpResetPassword(OnClearOtpResetPasswordEvent event) {
        String username = event.getUsername();
        customUserRepository.findAndUpdatePasswordResetCode(username, null);
    }
}
