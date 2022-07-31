package com.vnpt.intership.news.api.v1.event.listener;

import com.vnpt.intership.news.api.v1.domain.entity.AuthIdentityEntity;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.event.OnSaveRefreshTokenAndDeviceMetaEvent;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SaveRefreshTokenAndDeviceMetaListener {
    @Autowired
    private UserRepository userRepository;

    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK, classes = {Exception.class})
    public void saveRefreshTokenAndDeviceMeta(OnSaveRefreshTokenAndDeviceMetaEvent event) {
        User user = event.getUser();
        DeviceMeta deviceMeta = event.getDeviceMeta();
        UserEntity userEntity = userRepository.findByUsername(user.getUsername().trim())
                .orElseThrow(() -> new UserNotFoundException("Username or password wrong!"));

        AuthIdentityEntity authIdentity = userEntity.getAuthIdentity();
        if (authIdentity == null) {
            authIdentity = new AuthIdentityEntity();
        } else {
            Set<DeviceMeta> deviceMetas = authIdentity.getDeviceMetas().stream()
                    .map(device -> {
                        if (device.getLocation().equals(deviceMeta.getLocation())
                                && device.getDeviceDetails().equals(deviceMeta.getDeviceDetails())) {
                            device.setRefreshToken(deviceMeta.getRefreshToken());
                        }
                        return device;
                    }).collect(Collectors.toSet());
            authIdentity.setDeviceMetas(deviceMetas);
        }

        deviceMeta.setRefreshToken(deviceMeta.getRefreshToken());
        authIdentity.getDeviceMetas().add(deviceMeta);

        userEntity.setAuthIdentity(authIdentity);
        userRepository.save(userEntity);
    }
}
