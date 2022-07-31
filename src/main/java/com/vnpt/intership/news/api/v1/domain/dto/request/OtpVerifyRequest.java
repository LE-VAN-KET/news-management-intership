package com.vnpt.intership.news.api.v1.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    @NotNull
    @NotEmpty
    @Size(min = 6, max = 6)
    private String otp;
}
