package com.vnpt.intership.news.api.v1.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnClearOtpResetPasswordEvent extends ApplicationEvent {
    private String username;

    public OnClearOtpResetPasswordEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
