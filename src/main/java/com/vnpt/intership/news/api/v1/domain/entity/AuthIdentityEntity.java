package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class AuthIdentityEntity {
    // time to live refresh token 5 minutes
    @Indexed(expireAfterSeconds = 300)
    private String refreshToken;

    // time to live verify code 2 minutes
    @Indexed(expireAfterSeconds = 120)
    private String passwordResetCode;
}
