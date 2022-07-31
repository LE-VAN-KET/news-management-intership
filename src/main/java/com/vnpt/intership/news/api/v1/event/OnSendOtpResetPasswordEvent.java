package com.vnpt.intership.news.api.v1.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnSendOtpResetPasswordEvent extends ApplicationEvent {
    private String username;

    public OnSendOtpResetPasswordEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
