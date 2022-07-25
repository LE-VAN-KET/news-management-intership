package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DeviceMeta {
    // time to live refresh token 7 days
    @Indexed(expireAfterSeconds = 604800)
    private String refreshToken;

    private String deviceDetails;
    private String location;
}
