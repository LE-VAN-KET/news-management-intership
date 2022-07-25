package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AuthIdentityEntity {
    //list refresh token every device
    private Set<DeviceMeta> deviceMetas = new HashSet<>();

    // time to live verify code 2 minutes
    @Indexed(expireAfterSeconds = 120)
    private String passwordResetCode;
}
