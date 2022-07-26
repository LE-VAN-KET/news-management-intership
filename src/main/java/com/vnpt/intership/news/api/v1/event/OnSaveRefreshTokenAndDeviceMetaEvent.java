package com.vnpt.intership.news.api.v1.event;

import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.User;

@Getter
public class OnSaveRefreshTokenAndDeviceMetaEvent extends ApplicationEvent {
    private User user;
    private DeviceMeta deviceMeta;

    public OnSaveRefreshTokenAndDeviceMetaEvent(User user, DeviceMeta deviceMeta) {
        super(user);
        this.user = user;
        this.deviceMeta = deviceMeta;
    }
}
