package com.vnpt.intership.news.api.v1.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AuthIdentityEntity {
    //list refresh token every device
    private Set<DeviceMeta> deviceMetas = new HashSet<>();

    // time to live verify code 2 minutes
    @Indexed(expireAfterSeconds = 120, direction = IndexDirection.DESCENDING)
    private String passwordResetCode;

    private LocalDateTime createTime;
}
